package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            BufferedReader in = null; 
            
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
                          in = new BufferedReader(new InputStreamReader(
                                            echoSocket.getInputStream()));
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
    	String request_string = "Request " + SharedMemory.nodeId + " ->" + rq_num 
    			               + "port: " + port;	
        try
        {     
        	 //String reply_string = null;
        	 int if_req = 0;
        	 String temp_string,reply_string;
        	 for(int i =0;i<100;)
    	      {  
        		 //threadMessage(SharedMemory.state);
    	    	  if(RicartAgrawala.sm.getState() == "REQUEST" && if_req == 0)
    	    	  {
    	    		  
    	    		     threadMessage("SENT:" + request_string);
    	    		     out.println(request_string);
    	    		     RicartAgrawala.sm.incrementRequestNum();
    	    		     
    	    		     if_req = 1;
    	    		     
    	    		     i++;
    	    	  }
    	    	  
    	    	  //sent reply
    	    	  temp_string = RicartAgrawala.sm.getRqHeadMember(rq_num);
    	    	  if(temp_string != null)
    	    	  {
    	    		  reply_string = "Reply "+ SharedMemory.nodeId +" ->" + rq_num
    	    				        +"port:" + port;
 	    		      out.println(reply_string);
    	    		  //SharedMemory.reply_num ++;
    	    	  }/*
    	    	  if(SharedMemory.state == "END")
    	    	  {
    	    		  break;
    	    	  }*/
    	       }
         
        	 out.close();
       	   in.close();
       	   //stdIn.close();
       	   echoSocket.close();
    
        }catch(IOException e) 
        {
        	
        }
        
      }
          
  }
