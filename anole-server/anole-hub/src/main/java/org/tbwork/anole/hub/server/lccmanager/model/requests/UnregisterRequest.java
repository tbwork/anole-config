package org.tbwork.anole.hub.server.lccmanager.model.requests;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor 
@NoArgsConstructor
public class UnregisterRequest{
	
	private int clientId; 
}
