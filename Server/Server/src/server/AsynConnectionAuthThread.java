/**
 * @author pollux
 *
 */
package server;

import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import exceptions.ConnectionTimeoutException;
import exceptions.ConnectionUnexpectedlyClosedException;
import exceptions.ProtocolErrorException;
import helper.OSDepPrint;
import network.ConnectionCodes;
import com.pathfinder.pathfindertestclient.shared.XMLObject;
import network.Connector;
// TODO 
// - implement the possibility to stop the thread from the outside 


public class AsynConnectionAuthThread extends Thread {
	private int ref = 0;
	private String client_uid;
	private Connector con;
	private String location;
	
	public AsynConnectionAuthThread(Socket _client, int _ref) {
		ref = _ref;
		try {
			con = new Connector(_client, _ref);
			
		} catch (Exception e) {
			e.printStackTrace();
			OSDepPrint.error("Failed to initialize networking component", ref);
			// TODO stop thread
		}
		try {
			location = AsynConnectionAuthThread.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			location = location.substring(0,  location.length()-10); //server.jar 10 chars
			ResourceManager.location = location;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	// ##################################################################################
	// AUTHENTICATION

	
	public void run(){
		// process request
		int response;
		try {
			response = con.getCode(5000);
			switch(response){
	        case ConnectionCodes.REQUEST: 	request();
	        }
		} catch (InterruptedException e) {
			// special treatment?
		} catch (Exception e) {
			//return;
		}
		
		AsynServerListener.unregister(ref, this);
	}
	
	/**
	 * Exchanges the public, session keys and validates the UID. Then filters the
	 * next incoming code and calls the corresponding request function.
	 * 
	 * @throws ProtocolErrorException 
	 * @throws ConnectionUnexpectedlyClosedException 
	 * @throws ConnectionTimeoutException 
	 * @throws InterruptedException 
	 * 
	 */
	private void request() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, InterruptedException{
		int req = 0;
		
		// exchange public keys		
		if((client_uid = con.keyExchange()).equals("0000")) {
			OSDepPrint.info("Authentication skipped", ref);
			respondRegister();
			con.disconnect();
			return;
		} else {
			if(UUIDManager.validateUUID(client_uid)) {
				OSDepPrint.info("Successfully authenticated", ref);
			} else {
				// we shouldn't end up here 
				return;
			}
		}
		
		req = con.getCode(5000);
		
		switch(req){
		case ConnectionCodes.REGISTER:	respondRegister();
										break;
		case ConnectionCodes.MAP: 		respondMap();
										break;
		case ConnectionCodes.SECTOR: 	respondSector();
										break;
		case ConnectionCodes.XML:		respondResource();
										break;
		case ConnectionCodes.IMAGE:		respondResource();
										break;
		case ConnectionCodes.TEXT:		respondResource();
										break;
		case ConnectionCodes.OBJECT:	respondObject();
										break;
		case ConnectionCodes.PATH:		respondPath();
										break;
		case ConnectionCodes.RETRY:		OSDepPrint.net("Retry requested", ref);
										con.retry();
										break;
		case -1:						// TODO Fehlerbehandlung
										break;
		case -2:						// TODO Fehlerbehandlung
										break;
		}
		
		con.disconnect();
	}
	
	/**
	 * Generates a UID and sends it to the client
	 * TODO implement
	 */
	private void respondRegister(){
		OSDepPrint.net("UID requested", ref);
		
		con.writeText(UUIDManager.generateNewUUID());
		OSDepPrint.debug("Successfully send UID to client", ref);
		
	}
	
	private void respondMap() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, InterruptedException {
		OSDepPrint.net("Map requested, sending file information...", ref);
		
		// responding pattern for maps
		int map_id = con.getCode();
		String map_location = ResourceManager.getMapLocation(map_id);
		String[] file_name = map_location.split("/");
		con.writeText(file_name[file_name.length-1]);
		//con.package_delay = con.calcAvgDelay();
		OSDepPrint.net("Uploading '" + file_name[file_name.length-1] + "'", ref);
		checkInterruption();
		con.sendFile(map_location,  0);
		
		OSDepPrint.net("Map file successfully uploaded", ref);
	}
	
	private void respondSector() throws ConnectionUnexpectedlyClosedException, ConnectionTimeoutException, InterruptedException, ProtocolErrorException {
		OSDepPrint.net("Map sector requested, generating submap...", ref);
		
		// responding pattern for sectors
		double[] sector = con.readSector();
		String ret;
		try {
			 ret = ResourceManager.getMap().generateSubmap(sector,  14,  "map_sector_" + ref + ".map");
		} catch (Exception e) {
			throw new InterruptedException(); // TODO either an own exception class or w/e
		}
		OSDepPrint.net("Uploading '" + ret.substring(ret.lastIndexOf("/")) + "'", ref);
		checkInterruption();
		con.sendFile(ret,  0);
		
		OSDepPrint.net("Submap successfully uploaded", ref);
	}
	
	private void respondResource() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, InterruptedException {
		OSDepPrint.net("Resource file requested, sending file information...", ref);
		
		// responding pattern for resources
		long item_id = con.readLong();
		String res_location = ResourceManager.returnFilePath(item_id);
		String[] file_name = res_location.split("/");
		con.writeText(file_name[file_name.length-1]);
		checkInterruption();
		con.sendFile(res_location,  0);
		OSDepPrint.net("Uploading '" + file_name[file_name.length-1] + "'", ref);
		OSDepPrint.net("Resource file successfully uploaded", ref);
	}
	
	private void respondObject() throws ConnectionUnexpectedlyClosedException, ConnectionTimeoutException, InterruptedException {
		OSDepPrint.net("Custom Object requested", ref);
		
		// responding pattern for objects
		long item_id = con.readLong();
		Object obj;
		if((obj = ResourceManager.returnObject(item_id))==null) {
			// invalid id
			OSDepPrint.net("Invalid id for object request", ref);
		}
		checkInterruption();
		con.writeObject(obj);
		OSDepPrint.net("Object successfully uploaded", ref);
	}

	private void respondPath() throws InterruptedException {
		OSDepPrint.net("Pathing request", ref);
		
		// TODO reference to pathing calculations
		
		// responding pattern for paths
		XMLObject xml = (XMLObject) con.readObject();
		OSDepPrint.info("Pathing value: " + xml.getValue(), ref);
		// testing values
		List<XMLObject> paths = new ArrayList<XMLObject>();
		paths.add(new XMLObject(32));
		paths.add(new XMLObject(42));
		
		checkInterruption();
		con.writeObject(paths);
		OSDepPrint.net("Paths successfully uploaded", ref);
	}
	
	public static void checkInterruption() throws InterruptedException {
		if(Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
	}
	
	

}
