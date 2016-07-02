package org.tbwork.anole.hub.server.boss.worker;

import org.tbwork.anole.hub.server.AnoleServer;

/**
 * Anole worker-manager server manages all the workers.
 * When an Anole client attempts to connect to an Anole
 * worker server, it connects to the boss server first,
 * then the boss server arranges a worker server to it
 * by a response which contains essential information
 * of connecting a worker server. 
 * @author tommy.tang
 */
public class AnoleWorkerManagerServer implements AnoleServer{

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
