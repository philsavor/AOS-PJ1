package main;

import java.net.ServerSocket;
import java.util.*;

public class SharedMemory {
    public static final int NODE_NUM = 5;
    public static final int TIME_UNIT = 50;
    public static int nodeId = 0;
    public static ServerSocket serverSocket = null;
    
    
	private Object lock_t = new Object();
	private Object lock_state = new Object();
	private Object lock_rq = new Object();
	private Object lock_rp = new Object();
	private Object lock_ir = new Object();
	private Object lock_ic = new Object();
	private Object lock_cs = new Object();
	private Object lock_cn = new Object();
	private Object lock_iz = new Object();
	private Object lock_mn = new Object();   //message_num
	private Object lock_tt = new Object();   //timestamp
	private Object lock_rt = new Object();   //request_timestamp
	
    //mutex
    private  int temp_num;
    private  String state ;
    private List<String> request_queue;
    private int reply_num ;
    private int[] if_request = new int[NODE_NUM];
    private int if_complete;
    private int if_zero_node_complete;
    private int cs_num ;
    private int complete_num;
    private int message_num;
    private int timestamp;
    private int request_timestamp;
    
    public SharedMemory() {
        this.temp_num = 0;
        this.state = "INIT";
        request_queue =  new ArrayList<String>();
        this.reply_num = 0;
        for(int i=0;i<NODE_NUM ; i++){
        	if_request[i] = 0;
        }
        this.if_complete = 0;
        this.cs_num = 0;
        this.complete_num = 0;
        this.if_zero_node_complete=0;
        this.message_num = 0;
        this.timestamp = 0;
        this.request_timestamp = 0;
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
    
    public void resetReplyNum(){
    	synchronized (lock_rp) {
            this.reply_num = 0;
        }
    }
    
    //complete num
    public void incrementCnNum(){
    	synchronized (lock_cn) {
            this.complete_num++;
        }
    }

    public int getCnNum(){
    	synchronized (lock_cn) {
            return this.complete_num;
        }
    }
    
  //cs num
    public void incrementCsNum(){
    	synchronized (lock_cs) {
            this.cs_num++;
        }
    }

    public int getCsNum(){
    	synchronized (lock_cs) {
            return this.cs_num;
        }
    }
    
    //message num
    public void incrementMnNum(int n){
    	synchronized (lock_mn) {
            this.message_num += n;
        }
    }

    public int getMnNum(){
    	synchronized (lock_mn) {
            return this.message_num;
        }
    }
    
    //timestamp
    public void incrementTtNum(int n){
    	synchronized (lock_tt) {
            this.timestamp += n;
        }
    }
    
    public void changeTtNum(int n){
    	synchronized (lock_tt) {
            this.timestamp = n;
        }
    }

    public int getTtNum(){
    	synchronized (lock_tt) {
            return this.timestamp;
        }
    }
    
    //request timestamp
    public void changeRtNum(int n){
    	synchronized (lock_rt) {
            this.request_timestamp = n;
        }
    }

    public int getRtNum(){
    	synchronized (lock_rt) {
            return this.request_timestamp;
        }
    }
    
    //if_complete
    public void setIcTrue(){
    	synchronized (lock_ic) {
           this.if_complete = 1;
        }
    }
    
    public int getIcNum(){
    	synchronized (lock_ic) {
            return this.if_complete;
        }
    }
    
    //if_zero_node_complete
    public void setIzTrue(){
    	synchronized (lock_iz) {
           this.if_zero_node_complete = 1;
        }
    }
    
    public int getIzNum(){
    	synchronized (lock_iz) {
            return this.if_zero_node_complete;
        }
    }
    
  //if_request
    public void setIrFalse(int index){
    	synchronized (lock_ir) {
            this.if_request[index] = 0;
        }
    }

    public void setIrTrue(int index){
    	synchronized (lock_ir) {
           this.if_request[index] = 1;
        }
    }
    
    public int getIrValue(int index){
    	synchronized (lock_ir) {
           return this.if_request[index];
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
    			for(int i = 0 ; i< request_queue.size(); i++){
    				String hm = request_queue.get(i);
         		    
         		    String delims = "[ ]+";
         		    String[] tokens = (hm).split(delims);
         		    int temp = Integer.parseInt(tokens[1]);
         		    //request timestamp
         		    int rt = Integer.parseInt(tokens[2]);
         		    if(index == temp){
         		    	if(this.getState() != "REQUEST" && this.getState() != "CS" ||
         		    	   this.getState() == "REQUEST" && this.getRtNum() > rt ||
         		    	   this.getState() == "REQUEST" && this.getRtNum() == rt &&
         		    	   temp < SharedMemory.nodeId)
         		    	{
                 		    request_queue.remove(i);
                            return hm;
         		    	}
         		    }
    			}
    			
    			return null;
     		    
    	   }else
    			return null;
        }
    }

}
