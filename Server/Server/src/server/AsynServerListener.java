/**
 * @author pollux
 *
 */
package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import helper.OSDepPrint;

public class AsynServerListener extends Thread {
	public static volatile HashMap<Integer, Thread> connections;
	private static volatile boolean terminate_flag = false;
	private static ServerSocket server;
	private Socket client;
	private int connCounter = 0;
	
	public AsynServerListener(int port) {

		// Server initialisieren
		connections = new HashMap<Integer, Thread>();
		try {
			server = new ServerSocket(port, 0, InetAddress.getByName("62.113.206.126")); //192.168.188.55 //62.113.206.126
		} catch (IOException e1) {
			e1.printStackTrace();
			OSDepPrint.error("Failed to bind Server");
			// TODO timeout for sockets?

		}	
	}

	public void run(){
		
		System.out.println("Server listening on " + server.getInetAddress().toString().replace("/", "") + ":" + server.getLocalPort());
		while(!terminate_flag){
			try {
				client = server.accept();
				Socket newClient = new Socket();
				newClient = client;	// ist das ne deep copy?
				connCounter++;
				System.out.println(" [ #" + connCounter + " ] " +  client.getInetAddress().toString().replace("/", "") + ":" + client.getLocalPort() + " connected");
				AsynConnectionAuthThread conn = new AsynConnectionAuthThread(newClient, connCounter);
				Date currDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				conn.setName(sdf.format(currDate) + ", " + client.getInetAddress().toString().replace("/", ""));
				conn.start();
				connections.put(connCounter,  conn);
				
			} catch (SocketException e) {
				OSDepPrint.info("Server listener closed");
			} catch (IOException e) {
				e.printStackTrace();
				OSDepPrint.error("Failed to accept client");
				// TODO stop thread
			}
			
		}
				
	}
	
	/**
	 * Sends an interrupt to every thread created by the AsynServerListener class.
	 * 
	 */
	public static void terminate() {
		terminate_flag = true;
		for(Map.Entry<Integer, Thread> entry : connections.entrySet()) {
			entry.getValue().interrupt();
		}
	}
	
	/**
	 * Unregisters the specified thread. Called at the end of thread life.
	 * 
	 * @param id	thread id
	 * @param t		thread object
	 */
	public static void unregister(int id, Thread t) {
		try {
			//t.join();
			connections.remove(id);
			OSDepPrint.debug("Thread " + id + " closed", 0);
			if(connections.isEmpty()&&terminate_flag) {
				server.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	



}
