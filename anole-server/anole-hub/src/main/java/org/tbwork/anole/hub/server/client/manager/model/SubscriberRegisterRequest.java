package org.tbwork.anole.hub.server.client.manager.model;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class SubscriberRegisterRequest implements BaseOperationRequest{

	private int clientId;
	private int token;
	private SocketChannel socketChannel;
	
}
