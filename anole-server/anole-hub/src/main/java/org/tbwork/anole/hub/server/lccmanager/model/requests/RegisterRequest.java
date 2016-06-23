package org.tbwork.anole.hub.server.lccmanager.model.requests;

import org.tbwork.anole.hub.server.lccmanager.model.requests.params.IRegisterParameter;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator.ClientType;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	private SocketChannel socketChannel; 
	private IRegisterParameter registerParameter;
	private ClientType clientType;
}
