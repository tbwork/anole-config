package org.tbwork.anole.subscriber.client;

public class StaticConfiguration {

	public static final int CONNECTION_CHECK_INTERVAL = 60 ; // second
	
	public static final int RETRIEVING_THREAD_POOL_SIZE = 4; 
	
	public static final int RETRIEVING_CONFIG_TIMEOUT_TIME = 1000; // millisecond
	
	public static final int RECONNECT_INTERVAL = 3; //second
	
	public static final String REMOTE_ADDRESS = "localhost";
	
	public static final int PORT = 8080;
}
