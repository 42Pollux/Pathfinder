/**
 * @author pollux
 *
 */
package server;

import java.util.Map;
import java.util.Scanner;

import helper.OSDepPrint;
import network.Cryptography;

public class main {

	public static void main(String[] args) {
		boolean active_console = true;
		
		// process parameters and initialize stuff
		if(args.length<1) {
			System.out.println("Usage: PathfinderServer <Port> [options...]");
			System.out.println("   --debug     Enables debug ouput");
			System.out.println("   --pbar      Shows a progress bar for uploads");
			
			return;
		} else {
			for(String argument : args) {
				//OSDepPrint.info("Argument: " + argument);
				switch(argument) {
				case "--pbar":		
					OSDepPrint.setPBar(true);
					break;
				case "--debug":		
					OSDepPrint.setDebug(true);
					break;
				}
			}
			
		}
		OSDepPrint.initialize();
		ResourceManager.initialize();
		Cryptography.initialize();
		
		// check for valid port
		int port;
		try {
			port = Integer.parseInt(args[0]);
			if(port<1024) {
				System.out.println("Invalid port \"" + args[0] + "\", must be higher than 1024");
				return;
			}
			if(port>65534) {
				System.out.println("Invalid port \"" + args[0] + "\", must be lower than 65535");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid port \"" + args[0] + "\", not a number");
			return;
		}
		
		// start the server listener
		AsynServerListener server = new AsynServerListener(port);
		server.start();
		
		// console
		Scanner inputReader = new Scanner(System.in);
		while(active_console) {
			String input = inputReader.nextLine();
			switch(input) {
			case "stop":
				active_console = false;
				AsynServerListener.terminate();
				break;
			case "list":
				OSDepPrint.net("Currently connected clients: " + AsynServerListener.connections.entrySet().size(), 0);
				for(Map.Entry<Integer, Thread> entry : AsynServerListener.connections.entrySet()) {
					OSDepPrint.net("thread_id: " + entry.getKey() + ", thread_info: " + entry.getValue().getName(), 0);
				}
				break;
			}
		}
		inputReader.close();
		
	}
	

}

