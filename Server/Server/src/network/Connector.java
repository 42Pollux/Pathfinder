/**
 * @author pollux
 *
 */
package network;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import exceptions.ConnectionTimeoutException;
import exceptions.ConnectionUnexpectedlyClosedException;
import exceptions.ProtocolErrorException;
import helper.OSDepPrint;
import network.Cryptography;
import server.AsynConnectionAuthThread;
import server.ResourceManager;

// TODO Fehlerfälle und Ausgabe (OSDepPrint.error(e.getMessage(), ref);)

public class Connector {
	private Socket client;
	private ObjectOutputStream out_data;
	private ObjectInputStream in_data;
	private int calculation_delay = 25;
	public int package_delay = 50;
	private int ref = 0;
	private String client_uid = null;
	private Cryptography crypt = null;
	private static int MAX_TIMEOUT = 10000;

	public Connector(Socket _client, int _ref) {
		client = _client;
		ref = _ref;
		try {		
			out_data = new ObjectOutputStream(client.getOutputStream());
			in_data = new ObjectInputStream(client.getInputStream());
			
			crypt = new Cryptography();
			
		} catch (IOException e) {
			e.printStackTrace();
			OSDepPrint.error("Failed to create client streams", ref);
		}
	}
	
	/**
	 * Performs the key exchange of the RSA public keys and the AES key.
	 * Current sequence of events:
	 * 1. Client sends his public key base64 encoded.
	 * 2. Server sends his public key base64 encoded.
	 * 3. Client sends the RSA encrypted and base64 encoded AES key (session key).
	 * 4. Client sends the AES encrypted and base64 encoded UID.
	 * 
	 * @return UID as string on success, null in case of a register request
	 * @throws ConnectionTimeoutException
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws InterruptedException 
	 */
	public String keyExchange() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, InterruptedException{
		String client_public_key = getTextResponse();
		if(client_public_key=="0000") return null;
		// TODO save client public key for later
		OSDepPrint.debug("[KEYEX] received client public key: " + shortenKey(client_public_key), ref);
		writeText(crypt.getRSAPublicKey_base64Format());
		OSDepPrint.debug("[KEYEX] sending public key: " + shortenKey(crypt.getRSAPublicKey_base64Format()), ref);

		
		String client_encrypted_aes_key = getTextResponse();
		OSDepPrint.debug("[KEYEX] received client encrypted AES key: " + shortenKey(client_encrypted_aes_key), ref);
		crypt.setAESKey(crypt.decryptAESKeyWithRSA(Base64.getDecoder().decode(client_encrypted_aes_key)));

		
		String client_encrypted_uid = getTextResponse();
		OSDepPrint.debug("[KEYEX] received client encrypted UID", ref);
		client_uid = crypt.decryptText(client_encrypted_uid);
		OSDepPrint.debug("[KEYEX] Client UID: " + client_uid, ref);

		return client_uid;
	}
	
	private String shortenKey(String key){
        char[] charKey = key.toCharArray();
        String retKey = "";
        if(key.length()>99){
            retKey = "" + charKey[0]+charKey[1]+charKey[2]+charKey[3]+".."+charKey[50]+charKey[51]+charKey[52]+charKey[53]+".."+charKey[96]+charKey[97]+charKey[98]+charKey[99];
        } else if(key.length()>49){
            retKey = "" + charKey[0]+charKey[1]+charKey[2]+charKey[3]+".."+charKey[25]+charKey[26]+charKey[27]+charKey[28]+".."+charKey[46]+charKey[47]+charKey[48]+charKey[49];
        } else if(key.length()>29){
            retKey = "" + charKey[0]+charKey[1]+charKey[2]+charKey[3]+".."+charKey[10]+charKey[11]+charKey[12]+charKey[13]+".."+charKey[26]+charKey[27]+charKey[28]+charKey[29];
        }

        return retKey;
    }
	
	/**
	 * Closes the socket that is bound to the current connection.
	 * If the operation fails we assume that the connection has
	 * already been closed by the client.
	 * 
	 * @return 0 for success or -1 if the client is already disconnected
	 */
	public int disconnect(){
		try {
			client.shutdownOutput();
			OSDepPrint.info("connection closed", ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			OSDepPrint.info("connection closed by client", ref);
			return -1;
		}
		return 0;
	}
	
	/**
	 * Puts the thread to sleep for a certain amount of time.
	 * 
	 * @param ms	time in milliseconds
	 * @throws InterruptedException 
	 */
	private void sleep(int ms) throws InterruptedException{
		TimeUnit.MILLISECONDS.sleep(ms);
	}
	
	/**
	 * DEPRECATED
	 * Puts the thread to sleep for a certain amount of time.
	 * 
	 * @param mics	time in microseconds
	 */
	private void tick(int mics){
		try {
            TimeUnit.MICROSECONDS.sleep(mics);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
	}
	
	/** 
	 * Get the current line number.
	 * 
	 * @return	current line number.
	 */
	public static int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[4].getLineNumber();
	}
	
	/**
	 * Waits a MAX_TIMEOUT amount of time for a response from the 
	 * connected client. The response should be in form of a code
	 * as seen in class 'ConnectionCodes'. Transmissions are always 
	 * terminated by the END-sequence.
	 * 
	 * @return				the received code
	 * @throws InterruptedException 
	 */
	public int getCode() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, InterruptedException {
		int timeout = 0;
		int ret_byte = 0;
		
		while(timeout<MAX_TIMEOUT){
            try {
                while(in_data.available()>0){
                    return in_data.read();
                }
            } catch (IOException e) {
            	// Assuming IOException is only thrown for "Stream closed" 
            	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", getCode", ref);
            }
            sleep(10);
            timeout += 10;

        }
		throw new ConnectionTimeoutException(getLineNumber() + ", getCode", ref);
	}
	
	public int getCode(int timeout_ms) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, InterruptedException {
		int timeout = 0;
		int ret_byte = 0;
		
		while(timeout<timeout_ms){
            try {
                while(in_data.available()>0){
                    return in_data.read();
                }
            } catch (IOException e) {
            	// Assuming IOException is only thrown for "Stream closed" 
            	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", getCode", ref);
            }
            sleep(10);
            timeout += 10;

        }
		throw new ConnectionTimeoutException(getLineNumber() + ", getCode", ref);
	}
	
	/**
	 * Reads in all characters from the current stream until the 
	 * new line character appears. Times out after MAX_TIMEOUT seconds.
	 * 
	 * @return Text response as string.
	 * @throws ConnectionTimeoutException
	 * @throws ConnectionUnexpectedlyClosedException
	 * @throws InterruptedException 
	 */
	private String getTextResponse() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, InterruptedException{
		String response = "";
		int timeout = 0;
		
		while(timeout<MAX_TIMEOUT){
            try {
                while(in_data.available()>0){
                	char a = in_data.readChar();
                	response = response + a;
                	if(a=='\n') return response.substring(0, response.length()-1);
                }
            } catch (IOException e) {
            	// Assuming IOException is only thrown for "Stream closed" 
            	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", getTextResponse", ref);
            }

            sleep(10);
            timeout += 10;

        }
		throw new ConnectionTimeoutException(getLineNumber() + ", getTextResponse", ref);
	}
	
	/**
	 * Writes a single byte to the output stream and flushes.
	 * 
	 * @param code			the byte written
	 */
	private void writeCode(int code){
        try {
            out_data.write(code);
            //out_data.write(ConnectionCodes.END);
            out_data.flush();
        } catch (IOException e) {
        	OSDepPrint.error(e.getMessage(), ref);
        }

    }
	
	/**
	 * Writes a string to the output stream and flushes.
	 * 
	 * @param str			the string written	
	 */
	public void writeText(String str){
		str = str + "\n";
        try {
        	out_data.writeChars(str);
            out_data.flush();
        } catch (IOException e) {
            OSDepPrint.error(e.getMessage(), ref);
        }

    }
	/**
	 * Writes a serializable object to the output stream and flushes.
	 * 
	 * @param obj	the object
	 */
	public void writeObject(Object obj) {
		try {
			// TODO might need extra function with timeout for very large objects
			out_data.writeObject(obj);
			out_data.flush();
		} catch (IOException e) {
			
		}
	}
	
	/**
	 * Writes a 8-byte sized long to the output stream and flushes.
	 * 
	 * @param l	the long
	 */
	private void writeLong(long l) {
        try {
            out_data.writeLong(l);
            //out_data.write(ConnectionCodes.END);
            out_data.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
	
	/**
	 * Reads a 8-byte sized long from the input stream.
	 * 
	 * @return	the read long
	 * @throws	ConnectionUnexpectedlyClosedException
	 * @throws	ConnectionTimeoutException
	 */
	public long readLong() throws ConnectionUnexpectedlyClosedException, ConnectionTimeoutException {
		int timeout = 0;
		while(timeout<MAX_TIMEOUT){
            try {
                while(in_data.available()>7){
                    return in_data.readLong();
                    
                }
            } catch (IOException e) {
            	// Assuming IOException is only thrown for "Stream closed" 
            	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", readLong", ref);
            }

            timeout += 10;

        }
		throw new ConnectionTimeoutException(getLineNumber() + ", readLong", ref);
	}
	
	/**
	 * Reads an object from the input stream.
	 * 
	 * @return	the read object
	 */
	public Object readObject(){ // TODO needs timeout?
        try {
            Object obj = in_data.readObject();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/**
	 * Reads four 4-byte sized floats from the input stream.
	 * 
	 * @return	the received map sector
	 * @throws 	ConnectionUnexpectedlyClosedException
	 * @throws 	ConnectionTimeoutException
	 */
	public float[] readSector() throws ConnectionUnexpectedlyClosedException, ConnectionTimeoutException {
		int timeout = 0;
		while(timeout<MAX_TIMEOUT){
            try {
                while(in_data.available()>15){
                	// float = 4 bytes, 1 sector = 4 float -> 16 bytes needed
                	float[] ret = new float[4];
                	ret[0] = in_data.readFloat();
                	ret[1] = in_data.readFloat();
                	ret[2] = in_data.readFloat();
                	ret[3] = in_data.readFloat();
                    return ret;
                    
                }
            } catch (IOException e) {
            	// Assuming IOException is only thrown for "Stream closed" 
            	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", readSector", ref);
            }

            timeout += 10;

        }
		throw new ConnectionTimeoutException(getLineNumber() + ", readSector", ref);
	}
	
	/**
	 * Receiving a long value. The long value represents the remaining
	 * bytes needed by the client for the requested file/map. The function
	 * then waits to receive the map code. It then starts
	 * the upload of the requested map by calling 'sendFile'.
	 * 
	 * @throws ProtocolErrorException 
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws ConnectionTimeoutException 
	 * @throws ProtocolErrorException
	 * @throws InterruptedException 
	 */
	public void retry() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, InterruptedException {
		int timeout = 0;
		long remainingBytes = 0;
		
		// get file size
		remainingBytes = readLong();
		
		int code = getCode(); // TODO if code==ConnectionCodes.SECTOR -> sector retry 
		// TODO implement switch statement
		try {
			sendFile(ResourceManager.getMapLocation(code), remainingBytes);
		} catch (ConnectionTimeoutException | ConnectionUnexpectedlyClosedException e) {
			OSDepPrint.error("File transfer incomplete", ref);
			return;
		}
		OSDepPrint.net("Map successfully reuploaded", ref);
		
        return;
	}
	
	/**
	 * Sends out the file size as type long. The function then proceeds
	 * to write 2 MB of data to the stream and wait for an 'ACK' from the
	 * client. The whole file is transferred as packages of 2 MB size.
	 * 
	 * @param filepath			path to the file that should be sent
	 * @param remainingBytes	bytes remaining for the client (for retries)
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws ConnectionTimeoutException 
	 * @throws ProtocolErrorException 
	 * @throws InterruptedException 
	 */
	public void sendFile(String filepath, long remainingBytes) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, InterruptedException{
		int curr = 0;
		long progress = 0;
		File filedata = new File(filepath);
		long fileSize = filedata.length();
		int timeout = 130 * 1000 * 2; // 260s timeout
		float dlspeed = 0.0f;
		DataInputStream in_file;
		
		
		// send file size
		writeLong(fileSize);
		OSDepPrint.debug("file name: " + filedata.getName() + ", file size: " + fileSize, ref);
		
		// send many 2mb packages
		try {
			in_file = new DataInputStream(new FileInputStream(filedata));
			
			byte[] buffer = new byte[1024 * 1024 * 2]; // 1048576*2
			do {
				long offset = 0;
				if(remainingBytes>0){
					do{
						curr = in_file.read(buffer, 0, buffer.length);
						progress += curr;
					} while((fileSize-remainingBytes)>progress);
					offset = (1024*1024 * 2) - (progress - (fileSize - remainingBytes));
					remainingBytes = 0;
				} else {
					curr = in_file.read(buffer, 0, buffer.length);
					progress += curr;
				}
			
				if(curr==-1) {
					in_file.close();
					break;
				}
				long seconds = sendCustomPackage(buffer, (int)offset, curr, timeout);
				AsynConnectionAuthThread.checkInterruption();
				dlspeed = 2048.0f/((float)seconds/1000.0f);

				
				OSDepPrint.printProgress(progress, fileSize-remainingBytes, dlspeed, ref);
				
			} while(curr>0);
			
			out_data.close();
			in_file.close();
			OSDepPrint.printProgress(progress, fileSize-remainingBytes, dlspeed, ref);
		} catch (IOException e) {
			OSDepPrint.printProgressStop();
			// Assuming IOException is only thrown for "Stream closed" 
        	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", sendFile", ref);
		}
		OSDepPrint.printProgressStop();
		return;
	}	
	
	/**
	 * Writes bytes from 'bytePackage' with the specified 'offset' and 'packageSize' 
	 * 
	 * @param bytePackage		byte-array containing the data
	 * @param offset			where to start in the byte-array
	 * @param packageSize		amount of bytes the function should write
	 * @param delay				DEPRECATED
	 * @return					time needed by the client to receive the file in milliseconds, -1 on error
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws ConnectionTimeoutException 
	 * @throws ProtocolErrorException 
	 * @throws InterruptedException 
	 */
	private long sendCustomPackage(byte[] bytePackage, int offset, int packageSize, int delay) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, InterruptedException {
		try {
			long time_start = System.nanoTime();

			// send the packet
			// TODO write is probably blocking if client can't receive fast enough, "nio"?
			out_data.write(bytePackage, offset, packageSize-offset);
			out_data.flush();
			
			
			// receive the time needed by the client (initial timeout of 130 seconds)
			// 260 seconds are needed to transmit 2 MB of data with 64KBit/s
			// 64KBit/s is the lowest acceptable bandwidth 
			if(getCode(delay)==ConnectionCodes.ACK){
				long time_needed = (System.nanoTime() - time_start);
				//OSDepPrint.debug("Time needed: " + ((time_needed/1000000) - calculation_delay - 2*package_delay), ref);
				return (time_needed/1000000) - calculation_delay - 2*package_delay;
			} else {
				// We should never get in here, however just to be sure let's put something here
				throw new ProtocolErrorException(getLineNumber() + ", sendCustomPackage, massive misbehaviour", ref);
			}
		} catch (IOException e) {
			// Assuming IOException is only thrown for "Stream closed" 
        	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", sendCustomPackage", ref);
		}
	}
	
	/**
	 * Calculates the average delay in milliseconds a package needs to travel from
	 * server to client and opposite. Five 'pings' are being sent to the client.
	 * 
	 * @return	the average delay in milliseconds
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws ConnectionTimeoutException 
	 * @throws InterruptedException 
	 */
	public int calcAvgDelay() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, InterruptedException {
		int avg = 0;
		String values = "";
		
		for(int i = 0; i<5; i++) {
			writeCode(ConnectionCodes.PING);
			long time_start = System.nanoTime();
			if(getCode()!=ConnectionCodes.PING) return -1;
			long time_needed = System.nanoTime() - time_start;
			time_needed = time_needed / 1000000;
			if((time_needed>10000)||(time_needed<1)) time_needed = 100;
			values = values + (time_needed/2) + ", ";
			avg += time_needed;
			sleep(200);
		}
		avg = (avg/5/2) - calculation_delay;
		OSDepPrint.info("Average delay to client " + avg + "ms (" + values.substring(0, values.length()-2) + ", cd=25ms)", ref);
		
		return avg;
	}

}
