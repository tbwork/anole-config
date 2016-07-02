package org.tbwork.anole.hub.server;

public interface AnoleServer {

	/**
	 * Start the server.
	 * @param port the port will be use.
	 */
	public void start(int port);
	
	
	/**
	 * Close the server.
	 */
	public void close();
	
	/**
	 * Return the port which is binded by the server.
	 */
	public int getPort();
	
}
