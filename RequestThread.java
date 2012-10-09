package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public  class RequestThread extends Thread {

        public void run()  {
        	
        	Socket echoSocket = null;
            PrintWriter out = null;
            //BufferedReader in = null; 
            
            //determine the port
            int port = 0;
            if (SharedMemory.temp_num != SharedMemory.nodeId)
            {
            	port = Nodes.getNode(SharedMemory.temp_num ++).getHostPort();
            }else
            {
            	SharedMemory.temp_num ++;
            	port = Nodes.getNode(SharedMemory.temp_num ++).getHostPort();
            }
          
            while(true){
            	try {
                         echoSocket = new Socket("localhost", port);
                          out = new PrintWriter(echoSocket.getOutputStream(), true);
                          //in = new BufferedReader(new InputStreamReader(
                           //                 echoSocket.getInputStream()));
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
    	String inputLine = "";	
        try{
    	    for (int i =0 ; i< 10 ; i++) {
    	    	inputLine = "";
    		    try {
        	          Thread.sleep(4000);
        	     } catch(InterruptedException ex) {
        	          Thread.currentThread().interrupt();
        	     }
    		   
    		   inputLine += SharedMemory.nodeId ;
        	
        	out.println(inputLine);
    	    }

    	out.close();
    	//in.close();
    	//stdIn.close();
    	echoSocket.close();
    	
        }catch(IOException e) {
        	
        }
      }
          
  }
