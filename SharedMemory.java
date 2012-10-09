package main;

import java.net.ServerSocket;

public class SharedMemory {
		 
	public static final int PORT_Node0 = 7000;
    public static final int PORT_Node1 = 7001;
    public static final int PORT_Node2 = 7002;
    
    public final static int NODE_NUM = 10;
    public static int temp_num = 0;
    public static int nodeId = 0;
    public static ServerSocket serverSocket = null;

}
