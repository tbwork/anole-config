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
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)  
	int ping_promise_count;
	boolean valid;
	
	public SubscriberClient(){}
	
	public SubscriberClient(int token, SocketChannel socketChannel){
		this.token = token;
		this.socketChannel = socketChannel;
		this.ping_promise_count = 0;
		this.valid =true;
	}
	
	public int addPingPromise()
	{
		return ++ ping_promise_count;
	}
	
	public int achievePingPromise()
	{
		return ping_promise_count >0 ? -- ping_promise_count : 0;
	}
	 
	public boolean maxPromiseCount()
	{
		return ping_promise_count >= StaticConfiguration.MAX_PROMISE_COUNT ;
	}
	
	public boolean testMaxPromiseCount()
	{
		return ping_promise_count+1 >= StaticConfiguration.MAX_PROMISE_COUNT ;
	}
}
