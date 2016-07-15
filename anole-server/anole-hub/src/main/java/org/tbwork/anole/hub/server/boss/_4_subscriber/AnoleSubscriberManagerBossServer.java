package org.tbwork.anole.hub.server.boss._4_subscriber;

import org.tbwork.anole.hub.server.AnoleServer;

/**
 * Anole subscriber's boss server only provides authentication
 * service for all subscriber clients. When a subscriber attempts
 * to connect to a worker server, it should connect to this server
 * to get the identification token in order to communicate with 
 * worker server.
 * @author tommy.tang
 */
public class AnoleSubscriberManagerBossServer implements AnoleServer{

	@Override
	public void start(int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	} 
	
}
