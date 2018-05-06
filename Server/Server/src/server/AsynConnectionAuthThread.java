/**
 * @author pollux
 *
 */
package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import network.ConnectionCodes;
import network.Cryptography;
import network.Connector;
// TODO 
// - implement the possibility to stop the thread from the outside 


public class AsynConnectionAuthThread extends Thread {
	private int ref = 0;
	private String client_uid = null;
	
	private Connector conn;
	
	public AsynConnectionAuthThread(Socket _client, int _ref) {
		ref = _ref;
		try {
			conn = new Connector(_client, _ref);
			
		} catch (Exception e) {
			e.printStackTrace();
			OSDepPrint.error("Failed to initialize networking component", ref);
			// TODO stop thread
		}
		
	}
	
	
	
	
	
	
	// ##################################################################################
	// AUTHENTICATION

	
	public void run(){
		
		// exchange public keys
		if(conn.keyExchange()<0) {
			OSDepPrint.error("Client failed to authenticate", ref);
			return;
		}
		
		// decrypt uid
		// TODO implement
		
		// validate uid
		// TODO implement
		
		// process request
		int response = conn.getCode(5000);
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
	
	/**
	 * Filters the next incoming code and calls the corresponding function.
	 * Performs a disconnect after the called functions finish.
	 * 
	 */
	private void request(){
		switch(conn.getCode(5000)){
		case ConnectionCodes.REGISTER:	OSDepPrint.net("UID requested", ref);
										register();
										break;
		case ConnectionCodes.MAP: 		conn.map();
										break;	
		case ConnectionCodes.MAPPART: 	OSDepPrint.net("Mappart requested", ref);
										OSDepPrint.info("Terminating", ref);
										// TODO Implement
										break;
		case ConnectionCodes.RETRY:		OSDepPrint.net("Retry requested", ref);
										conn.retry();
										break;
		case -1:						// TODO Fehlerbehandlung
										break;
		case -2:						// TODO Fehlerbehandlung
										break;
		}
		
		conn.disconnect();
	}
	
	/**
	 * Generates a UID and sends it to the client
	 * TODO rework with encryption
	 */
	private void register(){
		
		// TODO IMPLEMENT UID GENERATION
		// out.println("890fu8928f2893kat4g1q");
		OSDepPrint.debug("successfully send UID to client", ref);
		// TODO besserer check?
		
	}
	
	
	
	

}
