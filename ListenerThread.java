package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public  class ListenerThread extends Thread {
	
	private static Lock lock = new ReentrantLock(); 
	
    // Display a message, preceded by
    // the name of the current thread
    static void threadMessage(String message) {
        String threadName =
            Thread.currentThread().getName();
        System.out.format("%s: %s%n",
                          threadName,
                          message);
    }

	public void run()  {
	try{
	            Socket clientSocket = null;
	            try {
	                clientSocket = SharedMemory.serverSocket.accept();
	            }
	            catch (IOException e) {
	                System.err.println("Accept failed.");
	                System.exit(1);
	            }

	            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	            System.out.println("Server started");
	        
	            String receive_string;
	            while ((receive_string = in.readLine()) != null) {
	            	String delims = "[ ]+";
	        	    String[] tokens = (receive_string).split(delims);
	        	    //request
	        	    if (tokens[0].equals("Request")){
	        	    	 lock.lock();
	        	    	 int temp_tt = Integer.parseInt(tokens[2]);
	        	    	 if (temp_tt > RicartAgrawala.sm.getTtNum())
	        	    		 RicartAgrawala.sm.changeTtNum(temp_tt+1);
	        	    	 else 
	        	    		 RicartAgrawala.sm.incrementTtNum(1);
	        	    	 lock.unlock();
	        	    	 
	            	     RicartAgrawala.sm.addRequest(receive_string);
	        	    }else if (tokens[0].equals("Reply")){
	        	    	 lock.lock();
	        	    	 int temp_tt = Integer.parseInt(tokens[2]);
	        	    	 if (temp_tt > RicartAgrawala.sm.getTtNum())
	        	    		 RicartAgrawala.sm.changeTtNum(temp_tt+1);
	        	    	 else 
	        	    		 RicartAgrawala.sm.incrementTtNum(1);
	        	    	 lock.unlock();
	        	    	
	        	    	 //to compute the number of messages 
	        	    	 RicartAgrawala.sm.incrementMnNum(1);
	        	    	 
	        	    	 threadMessage("RECEIVE:" + receive_string);
	        	    	 RicartAgrawala.sm.incrementReplyNum();
	        	    }else if (tokens[0].equals("Complete")){
	        	    	 threadMessage("RECEIVE:" + receive_string);
	        	    	 RicartAgrawala.sm.incrementCnNum();
	        	    	 
	        	    	 //compute the number of messages
	        	    	 RicartAgrawala.sm.incrementMnNum(Integer.parseInt(tokens[1]));
	        	    }
	            }
	            
	            out.close();
	            in.close();
	            clientSocket.close();
	            SharedMemory.serverSocket.close();
	            
	   } catch (IOException e) {}
	}
}

