/**
 * 
 */
package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * @author pollux
 *
 */
public class AsynConnectionThread extends Thread {
	private Socket client;
	private PrintWriter out;
	private BufferedReader in;
	private DataOutputStream out_data;
	private DataInputStream in_data;
	private int ref = 0;
	private int clientSpeed = 5120;
	
	public AsynConnectionThread(Socket _client, int _ref) {
		client = _client;
		ref = _ref;
		try {
			out = new PrintWriter(_client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(_client.getInputStream()));
			
			out_data = new DataOutputStream(new BufferedOutputStream(_client.getOutputStream()));
			in_data = new DataInputStream(new BufferedInputStream(_client.getInputStream()));
			
		} catch (IOException e) {
			e.printStackTrace();
			OSDepPrint.error("Failed to create client streams", ref);
			// TODO stop thread
		}
		
	}

	public void run(){
		
		int response = getCode(5000);
		if(response >= 1){
			switch(response){
	        case ConnectionCodes.REQUEST: 	request();
											return;
	        }
		} else if(response==-2){
			OSDepPrint.error("Exception occurred", ref);
		} else {
			OSDepPrint.error("No response within time limit", ref);
		}
		
	}
	
	private int disconnect(){
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
	
	private void sleep(int milliseconds){
		try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
	}
	
	private void tick(int microseconds){
		try {
            TimeUnit.MICROSECONDS.sleep(microseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
	}
	
	private int getCode(int timeoutInMilliseconds){
		int timeout = 0;
		int ret_byte = -2;
		
		while(timeout<timeoutInMilliseconds){
            try {
                while(in_data.available()>0){
                    int tmp_byte = in_data.read();
                    OSDepPrint.debug("ready byte: " + tmp_byte, ref);
                    if(tmp_byte==ConnectionCodes.END) return ret_byte;
                    ret_byte = tmp_byte;
                    
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return -2;
            }

            sleep(250);
            timeout += 250;

        }
		return -1;
	}
	
	
	private String getTextResponse(){
		String response = "";
		int timeout = 0;
		
		while(timeout<5000){
            try {
                while(in.ready()){
                    int tmp_char = in.read();
                    String tmp_string = Character.toString((char) tmp_char);
                    response += tmp_string;
                    if("\n".equals(tmp_string)) return response;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return null;
            }

            sleep(250);
            timeout += 250;

        }
		return null;
	}
	

	private void request(){
		switch(getCode(5000)){
		case ConnectionCodes.REGISTER:	OSDepPrint.net("UID requested", ref);
										register();
										break;
		case ConnectionCodes.MAP: 		map();
										break;	
		case ConnectionCodes.MAPPART: 	OSDepPrint.net("Mappart requested", ref);
										OSDepPrint.info("Terminating", ref);
										// TODO Implement
										break;
		case ConnectionCodes.RETRY:		OSDepPrint.net("Retry requested", ref);
										retry();
										break;
		case -1:						// TODO Fehlerbehandlung
										break;
		case -2:						// TODO Fehlerbehandlung
										break;
		}
		
		disconnect();
	}
	
	private void register(){
		
		// TODO IMPLEMENT UID GENERATION
		out.println("890fu8928f2893kat4g1q");
		OSDepPrint.debug("successfully send UID to client", ref);
		// TODO besserer check?
		
	}
	
	private int map(){
		
		int mapcode = getCode(5000);
		if(mapcode<=0){
			// TODO Fehlerbehandlung
			return -1;
		}
		OSDepPrint.net("Map requested (" + mapcode + ")", ref);
		if(sendFile("/home/pollux/programms/video.mp4", 0)<0){
			OSDepPrint.error("File transfer incomplete");
			return -1;
		}
		OSDepPrint.net("Map successfully uploaded", ref);
		
		return 0;
	}
	
	private int retry(){
		int timeout = 0;
		long remainingBytes = -1;
		while(timeout<5000){
            try {
                while(in_data.available()>8){
                    long tmp_byte = in_data.readLong();
                    remainingBytes = tmp_byte;
                    tmp_byte = in_data.read();
                    if(tmp_byte!=ConnectionCodes.END){					// TODO check if integer long comp works
                    	remainingBytes = -2;
                    }
                    timeout = 5000;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return -1;
            }

            if(timeout<5000) sleep(250);
            timeout += 250;

        }
		if(remainingBytes<0) return -1;
		
		int code = getCode(5000);
		// TODO implement switch statement
		if(sendFile("/home/pollux/programms/video.mp4", remainingBytes)<0){
			OSDepPrint.error("File transfer incomplete");
			return -1;
		}
		OSDepPrint.net("Map successfully reuploaded", ref);
		
        return 0;
	}

	private int sendFile(String filepath, long remainingBytes){
		int curr = 0;
		int tickCount = 0;
		long progress = 0;
		long oldprogress = 0;
		File filedata = new File(filepath);
		long fileSize = filedata.length();
		long dlspeedtimeout = 160000; // 160sek initial timeout
		
		
		//send file size
		try {
			out_data.writeLong(fileSize);
			OSDepPrint.debug("filesize: " + fileSize, ref);
			out_data.write(ConnectionCodes.END);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//send file
		try {
			DataInputStream in_file = new DataInputStream(new FileInputStream(filedata));
			
			byte[] buffer = new byte[1048576];
			do {
				curr = in_file.read(buffer, 0, buffer.length);
				progress += curr;
				if(curr==-1) {
					in_file.close();
					break;
				}
				out_data.write(buffer, 0, curr);
				out_data.flush();

				if((progress-oldprogress)>=(clientSpeed*1024)){
					OSDepPrint.printProgress(progress, fileSize-remainingBytes, ref);
					oldprogress = progress;
					sleep(900);
				}	
			} while(curr>0);
			
			out_data.close();
			OSDepPrint.printProgress(progress, fileSize-remainingBytes, ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			OSDepPrint.printProgressStop();
			return -1;
		}
		OSDepPrint.printProgressStop();
		return 0;
	}
	

}
