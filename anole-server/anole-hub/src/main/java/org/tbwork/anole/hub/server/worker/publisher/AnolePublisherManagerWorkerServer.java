package org.tbwork.anole.hub.server.worker.publisher;

import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.server.AnoleServer;

import lombok.Data;

/**
 * An Anole publisher server manages all publisher clients.
 * A publisher is always tring to change certain configuration. 
 * @author Tommy.Tang
 */
@Service("publishServer")  
public class AnolePublisherManagerWorkerServer implements AnoleServer{

	private int port;

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
