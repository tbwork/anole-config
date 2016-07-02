package org.tbwork.anole.hub.server.lccmanager.model.requests;
  

import org.tbwork.anole.common.enums.ClientType;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	private SocketChannel socketChannel; 
	private RegisterParameter registerParameter;
	private ClientType clientType; 
}
