package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class RicartAgrawala {
	
	public static SharedMemory sm = new SharedMemory();
	
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
		
		//9 request thread
		(new SenderThread()).start();
		(new SenderThread()).start();
		(new SenderThread()).start();
		(new SenderThread()).start();
		(new SenderThread()).start();
		(new SenderThread()).start();
		(new SenderThread()).start();
		(new SenderThread()).start();
		(new SenderThread()).start();
		
		
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
	
		System.out.println("INIT");
		while(true) 
	      {
	    	  if(sm.getState() == "INIT")
	    	  {
	    		    if(sm.getCsNum()>=20 && SharedMemory.nodeId % 2 == 0){
	    		    	//sleep [40,50] time unit
		    		    Random randomGenerator = new Random();
		                int randomInt = randomGenerator.nextInt(11)+40;
		    		    try {
		        	          Thread.sleep(randomInt * SharedMemory.TIME_UNIT);
		        	     } catch(InterruptedException ex) {
		        	          Thread.currentThread().interrupt();
		        	     }
	    		    }else{
	    		    	//sleep [10,20] time unit
		    		    Random randomGenerator = new Random();
		                int randomInt = randomGenerator.nextInt(11)+10;
		    		    try {
		        	          Thread.sleep(randomInt * SharedMemory.TIME_UNIT);
		        	     } catch(InterruptedException ex) {
		        	          Thread.currentThread().interrupt();
		        	     }
	    		    }
	    		  
	    		    //to reset environment
	    		    for(int i=0 ; i<SharedMemory.NODE_NUM;i++){
	    		    	 sm.setIrFalse(i);
	    		    }
	    		    sm.resetReplyNum();
	    		    sm.changeState("REQUEST") ;
	    		    
	    	  }
	    	  
	    	  if(sm.getReplyNum() == SharedMemory.NODE_NUM -1 &&
	    			  sm.getState() != "COMPLETE")
	    	  {
	    		  System.out.println("CS");
	    		  try {
        	          Thread.sleep(3 * SharedMemory.TIME_UNIT);
        	      } catch(InterruptedException ex) {
        	          Thread.currentThread().interrupt();
        	      }
	    		  sm.incrementCsNum();
	    		  sm.changeState("INIT");
	    		  
	    	  }
	    	  if (sm.getState() != "COMPLETE" && sm.getCsNum() == 40)
	    	  {
	    		  System.out.println("COMPLETE");
	    		  sm.changeState("COMPLETE");
	    		  
	    		  
	    	  }
	    	  
	    	  //end
	    	  if(sm.getCnNum() == SharedMemory.NODE_NUM ){
	    		  System.out.println("END!!!");
	    		  break;
	    	  }
	    	
	    }

		//logger.info("system has started");
		
	}
}
