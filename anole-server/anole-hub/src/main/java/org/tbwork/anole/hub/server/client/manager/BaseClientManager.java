package org.tbwork.anole.hub.server.client.manager;

import org.tbwork.anole.hub.server.client.manager.model.BaseOperationRequest;

import io.netty.channel.socket.SocketChannel;

public interface BaseClientManager {

	public boolean validte(BaseOperationRequest request) ;

	public void unregisterClient(BaseOperationRequest request);
	
	public void registerClient(BaseOperationRequest request);
}
