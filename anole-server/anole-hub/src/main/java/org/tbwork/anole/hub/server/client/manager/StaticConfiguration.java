package org.tbwork.anole.hub.server.client.manager;

public class StaticConfiguration {

	/**
	 * <p>Why "MAX + 1" ?
	 * <p>Think about one situation that the server already 
	 * sent a PingMessage to the client. Before the server
	 * receives the response PingAckMessage, the ping thread
	 * or the scavenger thread runs and will detect that the 
	 * no_response_count of one client is MAX, and then mark  
	 * it as invalid or unregister it. To prevent this problem
	 * , we just need toincrease the set MAX_NO_RESPONSE_COUNT 
	 * by one!
	 */
	public static final int MAX_PROMISE_COUNT = 5 + 1;
	public static final int PING_PERIOD_SECOND = 5*1000; //  ms 
	public static final int SCAVENGER_PERIOD_BY_TIMES_OF_PING_PERIOD_SECOND = 6 ; // 6*PING_PERIOD_SECOND
}
