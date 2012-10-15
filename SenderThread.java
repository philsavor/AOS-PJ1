package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.*;
import java.io.*;

public  class SenderThread extends Thread 
{
	 private static Lock lock = new ReentrantLock(); 
	
	 static void threadMessage(String message) {
	        String threadName =
	            Thread.currentThread().getName();
	        String out_string = String.format("%s: %s%n",
	                          threadName,
	                          message);
	        
	        try {
	            BufferedWriter out = new BufferedWriter(new FileWriter("out.txt",true));
	            out.write(out_string);
	            out.close();
	        } catch (IOException e) {
	        }
	    }
	 
        public void run()  
        {
        	Socket echoSocket = null;
            PrintWriter out = null;
            
            //determine the port
            int port = 0, rq_num = 0;
            lock.lock();
            if (RicartAgrawala.sm.getTempNum() != SharedMemory.nodeId)
            {
            	port = Nodes.getNode(RicartAgrawala.sm.getTempNum()).getHostPort();
            	rq_num = RicartAgrawala.sm.getTempNum();
            	RicartAgrawala.sm.incrementTempNum();
            }else
            {
            	RicartAgrawala.sm.incrementTempNum();
            	port = Nodes.getNode(RicartAgrawala.sm.getTempNum()).getHostPort();
            	rq_num = RicartAgrawala.sm.getTempNum();
            	RicartAgrawala.sm.incrementTempNum();
            }
            lock.unlock();
          
            while(true)
            {
            	try 
            	{
                          echoSocket = new Socket("localhost", port);
                          out = new PrintWriter(echoSocket.getOutputStream(), true);
                          break;
                       } catch (UnknownHostException e) {
                            System.err.println("Don't know about host: localhost.");
                            //System.exit(1);
                       } catch (IOException e) {
                            // System.err.println("Couldn't get I/O for "
                            //       + "the connection to: localhost.");
                              //System.exit(1);
                     }
            }
                    
    	//BufferedReader stdIn = new BufferedReader(
         //                              new InputStreamReader(System.in));
        try
        {     
        	 String temp_string,request_string,reply_string,complete_string;
        	 String end_string;
        	 while(true)
    	      {  
    	    	  if(RicartAgrawala.sm.getState() == "REQUEST" && 
    	    			  RicartAgrawala.sm.getIrValue(rq_num) == 0 &&
    	    			  RicartAgrawala.sm.getIfReplyValue(rq_num) == 0)
    	    	  {
    	    		     //format:Request nodeId request_timestamp
    	    		     int rt = RicartAgrawala.sm.getRtNum();
    	    		  
    	    		     //sent request message
    	    		     request_string = "Request " + SharedMemory.nodeId + " " + rt +
    	    		    		          " -> " + rq_num + " port: " + port;
    	    		     threadMessage("SENT:" + request_string);
    	    		     System.out.println("SENT:" + request_string);
    	    		     out.println(request_string);
    	    		     
    	    		     RicartAgrawala.sm.incrementMnNum(1);
    	    		     RicartAgrawala.sm.setIrTrue(rq_num);
    	    		     
    	    	  }
    	    	  
    	    	  if(RicartAgrawala.sm.getState() == "COMPLETE" && rq_num == 0 &&
    	    			  RicartAgrawala.sm.getIcNum() == 0)
    	    	  {
                         //RicartAgrawala.sm.incrementMnNum(1);
                         int message_number =  RicartAgrawala.sm.getMnNum();
                         
                         //compute max and min number of exchanged messages
                         int max_num = 0, min_num =RicartAgrawala.sm.getMnNum();
                         for(int j=0 ;j< 40 ;j++){
                        	 int num = RicartAgrawala.sm.getCsmNum(j);
                        	 if(num > max_num)
                        		 max_num = num;
                        	 if(num < min_num)
                        		 min_num = num;
                         }
                         
    	    		     complete_string = "Complete " + message_number
    	    		    		          + " " + max_num + " " + min_num 
    	    		    		          + " " + "Node: " + SharedMemory.nodeId ;
    	    		     
    	    		     //elapsed time
    	    		     for(int j=0;j<40;j++)
    	    		    	 complete_string += " " + RicartAgrawala.sm.getETime(j);
    	    		    	 
    	    		     threadMessage("SENT:" + complete_string);
    	    		     out.println(complete_string);
    	    		     
    	    		     RicartAgrawala.sm.setIcTrue();
    	    	  }
    	    	  
    	    	  if(RicartAgrawala.sm.getState() == "COMPLETE" && SharedMemory.nodeId == 0
    	    			  && RicartAgrawala.sm.getIzNum() == 0)
    	    	  {
    	    		  RicartAgrawala.sm.incrementCnNum();
    	    		  RicartAgrawala.sm.setIzTrue();
    	    	  }
    	    	  
    	    	  //sent reply
    	    	  if(RicartAgrawala.sm.getState() != "CS" ){
    	    		  temp_string = RicartAgrawala.sm.getRqHeadMember(rq_num);
        	    	  if(temp_string != null)
        	    	  {
        	    		  RicartAgrawala.sm.incrementTtNum(1);  //reply timestamp
        	    		 
        	    		  //format:Reply nodeId reply_timestamp
     	    		      int tt = RicartAgrawala.sm.getTtNum();
        	    		  reply_string = "Reply "+ SharedMemory.nodeId + " " + tt +
        	    				         " -> " + rq_num + " port: " + port;
     	    		      out.println(reply_string);
     	    		      
     	    		      RicartAgrawala.sm.setIfReplyFalse(rq_num);
        	    	  }
    	    	  }
    	    	  
    	    	  //sent end message
    	    	  int mark = 0;
    	    	  if(RicartAgrawala.sm.getState() == "END" && SharedMemory.nodeId == 0){
    	    		  end_string = "End "+ SharedMemory.nodeId +" -> " + rq_num + " port: " + port;
  		              out.println(end_string);
  		              mark = 1;
    	    	  }
    	    	  
    	    	  if(RicartAgrawala.sm.getState() == "END"  && mark == 1){
    	    		   try{
    	    		      Thread.sleep(10 * SharedMemory.TIME_UNIT);
        	           } catch(InterruptedException ex) {
        	                 Thread.currentThread().interrupt();
        	           }
    	    		  
    	    		  break;
    	    	  }
    	       }
         
           out.close();
       	   echoSocket.close();
    
        }catch(IOException e) 
        {
        	
        }
        
      }
          
  }
