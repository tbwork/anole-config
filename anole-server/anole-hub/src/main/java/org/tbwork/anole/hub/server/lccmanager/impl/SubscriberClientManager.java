package org.tbwork.anole.hub.server.lccmanager.impl; 
import io.netty.channel.socket.SocketChannel; 
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.PublisherClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClient;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.params.CustomerRegisterParameter;
import org.tbwork.anole.hub.server.lccmanager.model.requests.params.IRegisterParameter;  ;

/**
 * Manager for subscriber clients.
 * @author Tommy.Tang
 */
@Service("subscriberClientManager")
public class SubscriberClientManager extends LongConnectionClientManager{

	@Override
	protected LongConnectionClient createClient(int token, RegisterRequest registerRequest) { 
		return new PublisherClient(token, registerRequest.getSocketChannel());
	}

	@Override
	protected boolean validate(SocketChannel socketChannel, IRegisterParameter registerParameter) {
		CustomerRegisterParameter crp = (CustomerRegisterParameter) registerParameter;
		String username = crp.getUsername();
		String password = crp.getPassword();
		
		
		return false;
	}

 
}
