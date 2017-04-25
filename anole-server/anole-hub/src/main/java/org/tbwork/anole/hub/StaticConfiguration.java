package org.tbwork.anole.hub;

public class StaticConfiguration {

	/**
	 * <p>Why "MAX + 1" ?
	 * <p>Let's assume to set this value as 5. Think about one 
	 * situation that the server already add a ping promise count
	 * for certain client whose ping_promise_count is already four. 
	 * Before the server receives the promised PingMessage, the 
	 * promise-add thread runs and set 
	 * scavenger thread runs and will detect that the ping_promise_count 
	 * of the client is already MAX, and then unregister it. 
	 * To prevent this problem we just need to increase the set
	 * MAX_PROMISE_COUNT by one!
	 */
	public static final int MAX_PROMISE_COUNT = 5 + 1;
	public static final int PROMISE_PING_INTERVAL = 60*1000; //  ms 
	public static final int EXPIRE_TIME = 60*60*1000 ;// ms  :  1 hour
	public static final int CACHE_RECYCLE_INTERVAL = 60*1000; //ms  : 1min
	public static final int ESTIMATED_INSERT_KEY_LIFETIME = 10*1000; //ms : 10s
	
	/**
	 * Use by server to manage workers 
	 */
	public static final int WORKER_CLIENT_OPS_THREAD_POOL_SIZE = 5; 
	
	public static final int WORKER_RESPONSE_TIMEOUT = 5;// 5s
	
	public static final int CHANGE_NOTIFY_INTERVAL = 5 * 1000; // 5s
}
