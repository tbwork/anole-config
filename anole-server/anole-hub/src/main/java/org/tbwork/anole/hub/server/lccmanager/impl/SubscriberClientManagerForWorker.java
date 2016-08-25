package org.tbwork.anole.hub.server.lccmanager.impl;  
import org.springframework.stereotype.Service; 
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient; 
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClient; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;

import io.netty.channel.socket.SocketChannel; 

/**
 * Subscriber manager used for worker server.
 * @author Tommy.Tang
 */
@Service("subscriberClientManager")
public class SubscriberClientManagerForWorker extends LongConnectionClientManager{ 
	@Override
	protected LongConnectionClient createClient(int token, RegisterRequest registerRequest) { 
		return new SubscriberClient(token, registerRequest.getSocketChannel());
	} 
	 
	public void ackChangeNotify(int clientId, String key, long timestamp){
		SubscriberClient sc = (SubscriberClient)  lcMap.get(clientId);
		sc.ackChangeNotification(key, timestamp);
	}
 
	
	public void fillInformation(int clientId, SocketChannel socketChannel){
		SubscriberClient sc = (SubscriberClient)  lcMap.get(clientId);
		sc.setSocketChannel(socketChannel);
	}
}
