package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import exceptions.ConnectionTimeoutException;
import exceptions.ConnectionUnexpectedlyClosedException;
import exceptions.ProtocolErrorException;
import helper.OSDepPrint;

public class Connector {
	private Socket client;
	private PrintWriter out;
	private BufferedReader in;
	private DataOutputStream out_data;
	private DataInputStream in_data;
	private int calculation_delay = 25;
	private int package_delay = 50;
	private int ref = 0;
	private String client_uid = null;
	private Cryptography crypt = null;

	public Connector(Socket _client, int _ref) {
		client = _client;
		ref = _ref;
		try {
			out = new PrintWriter(_client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(_client.getInputStream()));
			
			out_data = new DataOutputStream(new BufferedOutputStream(_client.getOutputStream()));
			in_data = new DataInputStream(new BufferedInputStream(_client.getInputStream()));
			
			crypt = new Cryptography();
			
		} catch (IOException e) {
			e.printStackTrace();
			OSDepPrint.error("Failed to create client streams", ref);
			// TODO stop thread
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
	 * @return UID as string on success, null on error
	 * @throws ConnectionTimeoutException
	 * @throws ConnectionUnexpectedlyClosedException 
	 */
	public String keyExchange() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException{
		String client_public_key = getTextResponse();
		// TODO save client public key for later
		OSDepPrint.debug("[KEYEX] received client public key: " + client_public_key, ref);
		writeText(crypt.getRSAPublicKey_base64Format());
		OSDepPrint.debug("[KEYEX] sending public key: " + crypt.getRSAPublicKey_base64Format(), ref);

		
		String client_encrypted_aes_key = getTextResponse();
		OSDepPrint.debug("[KEYEX] received client encrypted AES key: " + client_encrypted_aes_key, ref);
		crypt.setAESKey(crypt.decryptAESKeyWithRSA(Base64.getDecoder().decode(client_encrypted_aes_key)));

		
		String client_encrypted_uid = getTextResponse();
		OSDepPrint.debug("[KEYEX] received client encrypted UID", ref);
		client_uid = crypt.decryptText(client_encrypted_uid);
		OSDepPrint.debug("[KEYEX] Client UID: " + client_uid, ref);

		return client_uid;
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
	 */
	private void sleep(int ms){
		try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
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
	
	/** Get the current line number.
	 * 
	 *  @return Current line number.
	 */
	public static int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}
	
	/**
	 * Waits a certain amount of time for a response from the 
	 * connected client. The response should be in form of a code
	 * as seen in class 'ConnectionCodes'. Transmissions are always 
	 * terminated by the END-sequence.
	 * 
	 * @param timeout_ms	time we wait for the response in milliseconds
	 * @return				the received code
	 */
	public int getCode(int timeout_ms) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException {
		int timeout = 0;
		int ret_byte = 0;
		
		while(timeout<timeout_ms){
            try {
                while(in_data.available()>0){
                    int tmp_byte = in_data.read();
                    //OSDepPrint.debug("ready byte: " + tmp_byte, ref);
                    if(tmp_byte==ConnectionCodes.END) return ret_byte;
                    ret_byte = tmp_byte;
                    
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
	 * new line character appears.
	 * 
	 * @return Text response as string.
	 * @throws ConnectionTimeoutException
	 * @throws ConnectionUnexpectedlyClosedException
	 */
	private String getTextResponse() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException{
		String response = "";
		int timeout = 0;
		
		while(timeout<10000){
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
	 * Writes a single byte to the output stream and flushes
	 * 
	 * @param code			the byte written
	 */
	private void writeCode(int code){
        try {
            out_data.write(code);
            out_data.write(ConnectionCodes.END);
            out_data.flush();
        } catch (IOException e) {
        	OSDepPrint.error(e.getMessage(), ref);
        }

    }
	
	/**
	 * Writes a string to the output stream and flushes
	 * 
	 * @param str			the string written	
	 */
	private void writeText(String str){
		str = str + "\n";
        try {
        	out_data.writeChars(str);
            out_data.flush();
        } catch (IOException e) {
            OSDepPrint.error(e.getMessage(), ref);
        }

    }
	
	/**
	 * Filters the next incoming code and starts the upload
	 * of the requested map by calling 'sendFile'.
	 * TODO implement a switch statement for the different file paths
	 *  
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws ConnectionTimeoutException 
	 * @throws ProtocolErrorException 
	 */
	public void map() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException{
		
		int mapcode = getCode(5000);

		OSDepPrint.net("Map requested (" + mapcode + ")", ref);
		
		// calculate average package delay
		package_delay = calcAvgDelay();
		
		try {
			sendFile("/home/michael/pathfinder/video.mp4", 0);
		} catch (ConnectionTimeoutException | ConnectionUnexpectedlyClosedException e) {
			OSDepPrint.error("File transfer incomplete", ref);
			return;
		}
		OSDepPrint.net("Map successfully uploaded", ref);
		
		return;
	}
	
	/**
	 * Waits for the next 9 bytes containing a 'long' value and the
	 * terminating END-sequence. The long value represents the remaining
	 * bytes needed by the client for the requested file/map. The function
	 * then waits to receive the map code. It then starts
	 * the upload of the requested map by calling 'sendFile'.
	 * 
	 * @throws ProtocolErrorException 
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws ConnectionTimeoutException 
	 * @throws ProtocolErrorException
	 */
	public void retry() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException {
		int timeout = 0;
		long remainingBytes = -1;
		while(timeout<5000){
            try {
                while(in_data.available()>8){
                    long tmp_byte = in_data.readLong();
                    remainingBytes = tmp_byte;
                    tmp_byte = in_data.read();
                    if(tmp_byte!=ConnectionCodes.END){																	// TODO check if integer long comp works
                    	throw new ProtocolErrorException(getLineNumber() + ", retry", ref);
                    }
                    timeout = 5000;
                    break;
                }
            } catch (IOException e) {
            	// Assuming IOException is only thrown for "Stream closed" 
            	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", retry", ref);
            }

            if(timeout<5000) sleep(250);
            timeout += 250;

        }
		
		int code = getCode(5000);
		// TODO implement switch statement
		try {
			sendFile("/home/michael/pathfinder/video.mp4", remainingBytes);
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
	 */
	private void sendFile(String filepath, long remainingBytes) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException{
		int curr = 0;
		long progress = 0;
		File filedata = new File(filepath);
		long fileSize = filedata.length();
		int timeout = 130 * 1000 * 2; // 260s timeout
		float dlspeed = 0.0f;
		
		
		// send file size
		try {
			out_data.writeLong(fileSize);
			OSDepPrint.debug("filesize: " + fileSize, ref);
			out_data.write(ConnectionCodes.END);
		} catch (IOException e) {
			// Assuming IOException is only thrown for "Stream closed" 
        	throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", sendFile", ref);
		}
		
		// send many 1mb packages
		try {
			DataInputStream in_file = new DataInputStream(new FileInputStream(filedata));
			
			byte[] buffer = new byte[1024 * 1024 * 2]; // 1048576
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
	 */
	private long sendCustomPackage(byte[] bytePackage, int offset, int packageSize, int delay) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException {
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
	 */
	private int calcAvgDelay() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException {
		int avg = 0;
		String values = "";
		
		for(int i = 0; i<5; i++) {
			writeCode(ConnectionCodes.PING);
			long time_start = System.nanoTime();
			if(getCode(5000)!=ConnectionCodes.PING) return -1;
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
