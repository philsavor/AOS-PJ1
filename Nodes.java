package main;


public enum Nodes {
	 n0 ("net11.utdallas.edu", "192.268.91.1", 8000),
	 n1 ("net12.utdallas.edu", "192.268.91.2", 8001),
	 n2 ("net13.utdallas.edu", "192.268.91.3", 8002),
	 n3 ("net14.utdallas.edu", "192.268.91.1", 8003),
	 n4 ("net15.utdallas.edu", "192.268.91.2", 8004),
	 n5 ("net16.utdallas.edu", "192.268.91.3", 8005),
	 n6 ("net17.utdallas.edu", "192.268.91.1", 8006),
	 n7 ("net18.utdallas.edu", "192.268.91.2", 8007),
	 n8 ("net19.utdallas.edu", "192.268.91.2", 8008),
	 n9 ("net20.utdallas.edu", "192.268.91.3", 8009);
	 
	 private final String hostName;
	 private final String hostIp;
	 private final int hostPort;
	 
	 Nodes(String hostName, String hostIp, int hostPort){
		 this.hostName = hostName;
		 this.hostIp = hostIp;
		 this.hostPort = hostPort;
	 }
	 
	 public String getHostName() {
			return hostName;
		}

		public String getHostIp() {
			return hostIp;
		}

		public int getHostPort() {
			return hostPort;
		}

		//Reverse Map maps an integer to corresponding enum
       public static Nodes getNode(int nodeId){
			 return Nodes.values()[nodeId];

	   }
}
