package main;

import java.io.IOException;
import java.net.ServerSocket;


public class RicartAgrawala {
	
	public static void main(String[] args) {		
		
		if (args.length > 0) {
		    try {
		    	SharedMemory.nodeId = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("Argument must be an integer");
		        System.exit(1);
		    }
	     }

		try {
		initializeNode(SharedMemory.nodeId);
		} catch (Exception e){
			//TODO add error handling
		}
	}
	
	public static void initializeNode(int nodeId) throws Exception {
		System.out.println("Initializing system ...\n");
		
		//9th request thread
		(new RequestThread()).start();
		(new RequestThread()).start();
		(new RequestThread()).start();
		(new RequestThread()).start();
		(new RequestThread()).start();
		(new RequestThread()).start();
		(new RequestThread()).start();
		(new RequestThread()).start();
		(new RequestThread()).start();
		
		
		//determine the port
		System.out.println(SharedMemory.nodeId);
	    int port = Nodes.getNode(SharedMemory.nodeId).getHostPort();
	    
	    try {
	    	SharedMemory.serverSocket = new ServerSocket(port);
	    }
	    catch (IOException e) {
	        System.err.println("Could not listen on port: " + port);
	        System.exit(1);
	    }
		
		(new ListenerThread()).start();
		(new ListenerThread()).start();
		(new ListenerThread()).start();
		(new ListenerThread()).start();
		(new ListenerThread()).start();
		(new ListenerThread()).start();
		(new ListenerThread()).start();
		(new ListenerThread()).start();
		(new ListenerThread()).start();
	

		//logger.info("system has started");
		
	}
}
