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
import org.uni.pathfinder.shared.XMLObject;

import Core.Path;
import PathingInterface.PathingInterface;
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
//		try {
//			//location = AsynConnectionAuthThread.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
//			location = location.substring(0,  location.length()-10); //server.jar 10 chars
//			ResourceManager.location = location;
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		ResourceManager.location = "/home/michael/pathfinder/";
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
			e.printStackTrace();
			OSDepPrint.error(e.getMessage(), ref);
		}
		
		AsynServerListener.unregister(ref, this);
	}
	
	/**
	 * Exchanges the public, session keys and validates the UID. Then filters the
	 * next incoming code and calls the corresponding request function.
	 * @throws Exception 
	 * 
	 */
	private void request() throws Exception{
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
		case ConnectionCodes.PATH:		respondPath(); 					//make new route
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
	 * @throws Exception 
	 */
	private void respondRegister() throws Exception{
		OSDepPrint.net("UID requested", ref);
		
		String newid = UUIDManager.generateNewUUID();
		if(!PathingInterface.notifyUserIDIntoDB(newid)) {
			con.writeText("0000");
			OSDepPrint.error("Couldn't add uuid to db", ref);
			throw new Exception();
		}
		con.writeText(newid);
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
	
	private void respondSector() throws Exception {
		OSDepPrint.net("Map sector requested, generating submap...", ref);
		
		// responding pattern for sectors
		double[] sector = con.readSector();
		String ret;
		ret = ResourceManager.getMap().generateSubmap(sector,  14,  "map_sector_" + ref + ".map");
		
		OSDepPrint.net("Uploading '" + ret.substring(ret.lastIndexOf("/")) + "'", ref);
		con.writeText("map_sector_" + ref + ".map");
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

	private void respondPath() throws Exception {
		OSDepPrint.net("Pathing request", ref);
		
		// TODO reference to pathing calculations
		
		// responding pattern for paths
		XMLObject xml = (XMLObject) con.readObject();
		ArrayList<String> data = xml.getDataList();
		for(int i=0; i<data.size()/2; i++) {
			OSDepPrint.debug(data.get(i), ref);
		}
		// 23.000000000, 54.64
		//1
		//WeightTime /WeightDiff
		//
		// 23.000000000, 45.23123534
		// 23.000000000, 45.23123534
		
		
		
		ArrayList<ArrayList<String>>inputs = new ArrayList<ArrayList<String>>();
		
		for(int i=0; i<data.size()/2; i=i+3) {
			ArrayList<String>pointDescription = new ArrayList<String>();
			String[] coordinates = data.get(i).split(",");
			pointDescription.add(coordinates[1].replace(" ", ""));
			pointDescription.add(coordinates[0].replace(" ", ""));
			String elevation = data.get(i+1);
			String weigthTime = data.get(i+2);			
			pointDescription.add(elevation);
			pointDescription.add(weigthTime);
			inputs.add(pointDescription);
			
		}
		ArrayList<Path> paths = PathingInterface.getNewRoutes(inputs, client_uid);
		
		XMLObject xmlret = new XMLObject();
		paths.forEach(path->{
			path.getVertices().forEach(vertex->{
				xmlret.addElement(vertex.Storage.getLatitude() + ", " + vertex.Storage.getLongitude());
			});
			xmlret.addElement("end");
		});
		
		
		
		checkInterruption();
		con.writeObject(xmlret);
		OSDepPrint.net("Paths successfully uploaded", ref);
		
	}
	
	public static void checkInterruption() throws InterruptedException {
		if(Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
	}
	
	

}
