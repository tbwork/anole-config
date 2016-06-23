package org.tbwork.anole.hub.server.lccmanager.model.clients;

import org.tbwork.anole.hub.StaticConfiguration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import io.netty.channel.socket.SocketChannel;

@Data
public class SubscriberClient extends LongConnectionClient{ 
	public SubscriberClient(int token, SocketChannel socketChannel){
		super(token, socketChannel);
	}
	 
}
