package org.tbwork.anole.hub.server.client.manager.model;

import org.tbwork.anole.hub.server.client.manager.StaticConfiguration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import io.netty.channel.socket.SocketChannel;

@Data
public class SubscriberClient {

	int token;
	SocketChannel socketChannel;
	@Getter(AccessLevel.NONE) 
	int no_response_count;
	boolean valid;
	
	public SubscriberClient(){}
	
	public SubscriberClient(int token, SocketChannel socketChannel){
		this.token = token;
		this.socketChannel = socketChannel;
		this.no_response_count = 0;
		this.valid =true;
	}
	
	public int increaseNoResponseCount()
	{
		return ++ no_response_count;
	}
	 
	public boolean maxNoResponsecount()
	{
		return no_response_count < StaticConfiguration.MAX_NO_RESPONSE_COUNT ;
	}
}
