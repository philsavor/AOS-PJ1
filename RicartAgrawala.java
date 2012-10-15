package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Date;

public class RicartAgrawala {
	
	public static SharedMemory sm = new SharedMemory();
	
	 static void mainThreadMessage(String message) {
		        String out_string = String.format("MainThread: %s%n",message);
		        
		        try {
		            BufferedWriter out = new BufferedWriter(new FileWriter("out.txt",true));
		            out.write(out_string);
		            out.close();
		        } catch (IOException e) {
		        }
	    }
	
	public static void main(String[] args) {		
		
		if (args.length > 0) {
		    try {
		    	SharedMemory.nodeId = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		    	mainThreadMessage("Argument must be an integer");
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
		mainThreadMessage("Initializing system ...\n");
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
		mainThreadMessage(Integer.toString(SharedMemory.nodeId));
	    int port = Nodes.getNode(SharedMemory.nodeId).getHostPort();
	    
	    try {
	    	SharedMemory.serverSocket = new ServerSocket(port);
	    }
	    catch (IOException e) {
	    	mainThreadMessage("Could not listen on port: " + port);
	        System.exit(1);
	    }
		
	    //9 listenner thread
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	    (new ListenerThread()).start();
	
	    mainThreadMessage("INIT");
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
	    		    for(int i=0 ; i<SharedMemory.NODE_NUM ; i++){
	    		    	 sm.setIrFalse(i);
	    		    }
	    		    sm.incrementTtNum(1);  //request timestamp
	    		    sm.changeRtNum(sm.getTtNum());
	    		    sm.changeState("REQUEST") ;
	    		    
	    		    long current_time = new Date().getTime(); 
	    		    sm.setReqTime(current_time);
	    		    
	    	  }
	    	  
	    	  int reply_num = 0;
	    	  for(int i=0; i< SharedMemory.NODE_NUM ;i++)
	    		  if(sm.getIfReplyValue(i) == 1)
	    			   reply_num ++;
	    	  if(reply_num == SharedMemory.NODE_NUM -1 &&
	    			  sm.getState() == "REQUEST")
	    	  {
	    		  sm.changeState("CS");
	    		  mainThreadMessage("CS");
	    		  System.out.println("CS");
	    		  try {
        	          Thread.sleep(3 * SharedMemory.TIME_UNIT);
        	      } catch(InterruptedException ex) {
        	          Thread.currentThread().interrupt();
        	      }
	    		  //set cs message number
	    		  if(sm.getCsmNum(0) == 0)
	    			  sm.setCsmNum(0, sm.getMnNum());
	    		  else{
	    			  int last_message_num = 0;
	    			  for(int i = 0;i<sm.getCsNum();i++)
	    				  last_message_num += sm.getCsmNum(i);
	    			  int temp_num = sm.getMnNum() - last_message_num;
	    			  sm.setCsmNum(sm.getCsNum(), temp_num);
	    			  
	    			  String temp = "CS messages num: " + Integer.toString(temp_num);
	    			  mainThreadMessage(temp);
	    		  }
	    		  
	    		  //set elapsed time
	    		  long current_time = new Date().getTime();
	    		  long et = current_time - sm.getReqTime();
	    		  sm.setETime(sm.getCsNum(), et);
	    		  String temp = "CS elapsed time: " + Long.toString(et);
	    		  mainThreadMessage(temp);
	    			
	    		  sm.incrementCsNum();
	    		  sm.changeState("INIT");
	    		  
	    	  }
	    	  if (sm.getState() != "COMPLETE" && sm.getState() !="END" && sm.getCsNum() == 40)
	    	  {
	    		  mainThreadMessage("COMPLETE");
	    		  System.out.println("COMPLETE");
	    		  sm.changeState("COMPLETE");
	    		  
	    	  }
	    	  
	    	  //end ,only zero node can satisfy
	    	  if(sm.getState()!= "END" && sm.getCnNum() == SharedMemory.NODE_NUM ){
	    		  //compute the number of message
	    		  mainThreadMessage("The total number of messages are " + sm.getMnNum());
	    		  
	    		  mainThreadMessage("END!!!");
	    		  System.out.println("END!!!"); 
	    		  sm.changeState("END");
	    	  }
	    	  
	    	  if(sm.getState()== "END" )
	    	  {
	    		  try{
	    		      Thread.sleep(10 * SharedMemory.TIME_UNIT);
    	           } catch(InterruptedException ex) {
    	                 Thread.currentThread().interrupt();
    	           }
	    		  
	    		  System.exit(0);
	    	  }
	    }
		
	}
}
