package main;

import java.net.ServerSocket;
import java.util.*;

public class SharedMemory {
    public static final int NODE_NUM = 5;
    public static final int TIME_UNIT = 50;
    public static int nodeId = 0;
    public static ServerSocket serverSocket = null;
    
	private Object lock_t = new Object();     //temp_num
	private Object lock_state = new Object();
	private Object lock_rq = new Object();   //request_queue
	private Object lock_rp = new Object();   //if_reply
	private Object lock_ir = new Object();   //if_request
	private Object lock_ic = new Object();   //if_complete
	private Object lock_cs = new Object();   
	private Object lock_cn = new Object();
	private Object lock_iz = new Object();
	private Object lock_mn = new Object();   //message_num
	private Object lock_tt = new Object();   //timestamp
	private Object lock_rt = new Object();   //request_timestamp
	private Object lock_csm = new Object();   //cs_message_num
	private Object lock_rtime = new Object();   //request_time
	private Object lock_et = new Object();   //elapsed_time
	
    //mutex
    private int temp_num;
    private String state ;
    private List<String> request_queue;
    private int[] if_reply = new int[NODE_NUM];
    private int[] if_request = new int[NODE_NUM];
    private int if_complete;
    private int if_zero_node_complete;
    private int cs_num ;
    private int complete_num;
    private int message_num;
    private int timestamp;
    private int request_timestamp;
    private int[] cs_message_num = new int[40] ;
    private long request_time;
    private long[] elapsed_time = new long[40];
    
    public SharedMemory() {
        this.temp_num = 0;
        this.state = "INIT";
        request_queue =  new ArrayList<String>();
        for(int i=0;i<NODE_NUM ; i++){
        	if_reply[i] = 0;
        }
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
        for(int i=0;i<40 ; i++){
        	cs_message_num[i] = 0;
        }
        this.request_time = 0;
        for(int i=0;i<NODE_NUM ; i++){
        	elapsed_time[i] = 0;
        }
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
    
    //cs message num
    public void setCsmNum(int cs_num , int n){
    	synchronized (lock_csm) {
            this.cs_message_num[cs_num] = n;
        }
    }

    public int getCsmNum(int index){
    	synchronized (lock_csm) {
            return this.cs_message_num[index];
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
    
    //request time
    public void setReqTime(long time){
    	synchronized (lock_rtime) {
            this.request_time = time;
        }
    }

    public long getReqTime(){
    	synchronized (lock_rtime) {
            return this.request_time;
        }
    }
    
    //elapsed time
    public void setETime(int index, long time){
    	synchronized (lock_et) {
            this.elapsed_time[index] = time;
        }
    }

    public long getETime(int index){
    	synchronized (lock_et) {
            return this.elapsed_time[index];
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
    
    
    //if_reply
    public void setIfReplyFalse(int index){
    	synchronized (lock_rp) {
            this.if_reply[index] = 0;
        }
    }

    public void setIfReplyTrue(int index){
    	synchronized (lock_rp) {
            this.if_reply[index] = 1;
        }
    }
    
    public int getIfReplyValue(int index){
    	synchronized (lock_rp) {
            return this.if_reply[index];
        }
    }
    
    //request_queue
    public void addRequest(String s){
    	synchronized (lock_rq) {
            this.request_queue.add(s);
        }
    }
    
    //to determin when to reply
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
