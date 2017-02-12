package org.tbwork.anole.subscriber.client._2_worker;

/**
 * Connection monitor watches the connection between the subscriber
 * and the worker server all the time. It sends ping message to the
 * worker server regularly, once it finds the connection is broken,
 * it will reconnect to an other worker server assigned by the boss
 * server.
 * @author tommy.tang
 */
public interface ConnectionMonitor {

	public void start();
	
	public void stop();
	
	public void restart();
	 
}
