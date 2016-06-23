package org.tbwork.anole.hub.server.lccmanager.model.clients;

import org.tbwork.anole.hub.StaticConfiguration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import io.netty.channel.socket.SocketChannel;

/**
 * A worker client is also a server provides publishing and 
 * subscribing services.
 * @author tommy.tang
 */
@Data
public class WorkerClient extends LongConnectionClient{

	/**
	 * The count of publisher clients in the worker server.
	 */
	private int publisherClientCount;
	
	/**
	 * The count of subscriber clients in the worker server.
	 */
	private int subscriberClientCount;
	
	
	/**
	 * The weight of the worker server.
	 */
	private int weight;
	
	/**
	 * The identity of the worker server.
	 */
	private String identity;
	
	public WorkerClient(int token, SocketChannel socketChannel, String identity){
		super(token, socketChannel);
		this.identity = identity;
		this.publisherClientCount = 0 ;
		this.subscriberClientCount = 0;
		this.weight = 10;
	}
}
