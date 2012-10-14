package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.*;

public  class SenderThread extends Thread 
{
	 private static Lock lock = new ReentrantLock(); 
	
	 static void threadMessage(String message) {
	        String threadName =
	            Thread.currentThread().getName();
	        System.out.format("%s: %s%n",
	                          threadName,
	                          message);
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
        	 for(int i =0;i<100;)
    	      {  
    	    	  if(RicartAgrawala.sm.getState() == "REQUEST" && 
    	    			  RicartAgrawala.sm.getIrValue(rq_num) == 0)
    	    	  {
    	    		     request_string = "Request " + SharedMemory.nodeId + " ->" + rq_num 
   			               + "port: " + port;
    	    		     threadMessage("SENT:" + request_string);
    	    		     out.println(request_string);
    	    		     
    	    		     RicartAgrawala.sm.setIrTrue(rq_num);
    	    		     
    	    		     i++;
    	    	  }
    	    	  
    	    	  if(RicartAgrawala.sm.getState() == "COMPLETE" && rq_num == 0 &&
    	    			  RicartAgrawala.sm.getIcNum() == 0)
    	    	  {
    	    		     complete_string = "Complete " + SharedMemory.nodeId;
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
    	    	  temp_string = RicartAgrawala.sm.getRqHeadMember(rq_num);
    	    	  if(temp_string != null)
    	    	  {
    	    		  reply_string = "Reply "+ SharedMemory.nodeId +" ->" + rq_num
    	    				        +"port:" + port;
 	    		      out.println(reply_string);
    	    		  //SharedMemory.reply_num ++;
    	    	  }
    	       }
         
           out.close();
       	   echoSocket.close();
    
        }catch(IOException e) 
        {
        	
        }
        
      }
          
  }
