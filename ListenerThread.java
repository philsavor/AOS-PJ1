package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.net.ServerSocket;
import java.net.Socket;
//import java.net.UnknownHostException;
import java.util.Random;

public  class ListenerThread extends Thread {
	
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

	            
	            System.out.println("Echo server started");
	            
	            ///////////////////////
	            //(new RequestThread1()).start();
	            ///////////////////////

	            String inputLine;
	            while ((inputLine = in.readLine()) != null) {
	            	
	            	//////////////////////////////////////
	            	try {
	            	    Thread.sleep(1000);
	            	} catch(InterruptedException ex) {
	            	    Thread.currentThread().interrupt();
	            	}
	            	inputLine += "sdf";
	            	
	            	Random randomGenerator = new Random();
	                int randomInt = randomGenerator.nextInt(10)+10;
	                  
	            	inputLine += Integer.toString(randomInt);
	            	///////////////////////////////////
	                System.out.println("echoing: " + inputLine);
	                out.println(inputLine);
	            }
	            out.close();
	            in.close();
	            clientSocket.close();
	            
	            SharedMemory.serverSocket.close();
	        	} catch (IOException e) {}
	        }
}

