package org.tbwork.anole.hub.server.client.manager.impl;

import io.netty.channel.socket.SocketChannel;

import org.springframework.stereotype.Service; 
import org.tbwork.anole.hub.server.client.manager.BaseClientManager;
import org.tbwork.anole.hub.server.client.manager.model.BaseOperationRequest;

@Service("publisherClientManager")
public class PublisherClientManager implements BaseClientManager {

	public boolean validte(BaseOperationRequest request) {
		// TODO Auto-generated method stub
		return false;
	}
 

	public void registerClient(BaseOperationRequest request) {
		 
	}


	public void unregisterClient(BaseOperationRequest request) {
		 
	}

}
