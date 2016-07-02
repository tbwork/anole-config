package org.tbwork.anole.hub.server.lccmanager.impl; 
import io.netty.channel.socket.SocketChannel; 
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient; 
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClient;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterParameter;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.util.ClientEntropyUtil; 

/**
 * Manager for subscriber clients.
 * @author Tommy.Tang
 */
@Service("subscriberClientManager")
public class SubscriberClientManager extends LongConnectionClientManager{

	@Override
	protected LongConnectionClient createClient(int token, RegisterRequest registerRequest) { 
		return new SubscriberClient(token, registerRequest.getSocketChannel());
	} 

 
}
