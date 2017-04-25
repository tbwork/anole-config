package org.tbwork.anole.hub.server.lccmanager.model.clients;

import org.tbwork.anole.hub.StaticConfiguration;

import io.netty.channel.socket.SocketChannel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * Provides basic member attributes and methods for all
 * long-connection client.
 * @author tommy.tang
 */
@Data
public class LongConnectionClientSkeleton {

	/**
	 * The unique identity code of one client 
	 * which is assigned by the client manager.
	 */
	int token;
	/**
	 * The socket channel of connection between
	 * the server and the client.
	 */
	SocketChannel socketChannel; 
	
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)  
	int ping_promise_count; 
	
	public LongConnectionClientSkeleton(){}
	
	public LongConnectionClientSkeleton(int token, SocketChannel socketChannel){
		this.token = token;
		this.socketChannel = socketChannel;
		this.ping_promise_count = 0; 
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
	
	
}
