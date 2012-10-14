package main;

import java.net.ServerSocket;
import java.util.*;

public class SharedMemory {
    public static final int NODE_NUM = 5;
    public static final int TIME_UNIT = 200;
    public static int nodeId = 0;
    public static ServerSocket serverSocket = null;
    
    
	private Object lock_t = new Object();
	private Object lock_state = new Object();
	private Object lock_rn = new Object();
	private Object lock_rq = new Object();
	private Object lock_rp = new Object();
	
    //mutex
    private  int temp_num=0;
    private  String state = "INIT";
    private int request_num = 0;
    private List<String> request_queue;
    private int reply_num = 0;
    
    //private int cs_num = 0;
    
    public SharedMemory() {
        this.temp_num = 0;
        this.state = "INIT";
        this.request_num = 0;
        request_queue =  new ArrayList<String>();
        this.reply_num = 0;
        //this.cs_num = 0;
    }
    
    public void incrementTempNum() {
        synchronized (lock_t) {
        	 temp_num++;
        }
    }  
    
    public int getTempNum() {
        synchronized (lock_t) {
            return temp_num;
        }
    }
    
    public String getState() {
        synchronized (lock_state) {
            return state;
        }
    }
    
    public void changeState(String s) {
        synchronized (lock_state) {
            state = s;
        }
    }
    
    //request num
    public void incrementRequestNum(){
    	synchronized (lock_rn) {
            this.request_num++;
        }
    }

    public int getRequestNum(){
    	synchronized (lock_rn) {
            return this.request_num;
        }
    }
    
    //reply num
    public void incrementReplyNum(){
    	synchronized (lock_rp) {
            this.reply_num++;
        }
    }

    public int getReplyNum(){
    	synchronized (lock_rp) {
            return this.reply_num;
        }
    }
    
    //request_queue
    public void addRequest(String s){
    	synchronized (lock_rq) {
            this.request_queue.add(s);
        }
    }
    
    public String getRqHeadMember(int index){
    	synchronized (lock_rq) {
    		if(request_queue.size()>0)
    		{
     		    String hm = request_queue.get(0);
     		    
     		    String delims = "[ ]+";
     		    String[] tokens = (hm).split(delims);
     		    int temp = Integer.parseInt(tokens[1]);
     		    
     		    if(index == temp){
         		    request_queue.remove(0);
                    return hm;
     		    }else
     		    	return null;
    		}else
    			return null;
        }
    }

}
