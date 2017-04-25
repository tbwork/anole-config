package org.tbwork.anole.hub.server.lccmanager.impl;  
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.common.model.ValueChangeDTO;
import org.tbwork.anole.hub.model.ConfigValueDO;
import org.tbwork.anole.hub.repository.ConfigRepository;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClientSkeleton; 
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClientSkeleton; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;

import io.netty.channel.socket.SocketChannel; 

/**
 * Subscriber manager used for worker server.
 * @author Tommy.Tang
 */
@Service("subscriberClientManager")
public class SubscriberClientManagerForWorker extends LongConnectionClientManager{ 
	
	@Autowired
	private ConfigRepository cr;
	
	@Override
	protected LongConnectionClientSkeleton createClient(int token, RegisterRequest registerRequest) {
		if(registerRequest.getRegisterParameter() == null || registerRequest.getRegisterParameter().getEnv() == null){
			throw new RuntimeException("Subscriber 's runtime environment should be specified.");
		}
		return new SubscriberClientSkeleton(token, registerRequest.getSocketChannel(), registerRequest.getRegisterParameter().getEnv());
	} 
	 
	public void ackChangeNotify(int clientId, String key, long timestamp){
		SubscriberClientSkeleton sc = (SubscriberClientSkeleton)  lcMap.get(clientId);
		if(sc == null)
			throw new RuntimeException("Cound not find subscriber client with id = "+ clientId);
		sc.ackChangeNotification(key, timestamp);
	}
	
	public void fillInformation(int clientId, SocketChannel socketChannel){
		SubscriberClientSkeleton sc = (SubscriberClientSkeleton)  lcMap.get(clientId);
		if(sc == null)
			throw new RuntimeException("Cound not find subscriber client with id = "+ clientId);
		sc.setSocketChannel(socketChannel);
	}
	 
	public void notifyChange(ValueChangeDTO vcd){
		Set<Entry<Integer, LongConnectionClientSkeleton>> entrySet = lcMap.entrySet();
		for(Entry<Integer, LongConnectionClientSkeleton> item : entrySet){
			SubscriberClientSkeleton sc = (SubscriberClientSkeleton)  item.getValue(); 
			cr.setConfigValue(vcd.getKey(), vcd.getValue(), vcd.getEnv(), vcd.getConfigType()); 
			sc.addNewChangeNotification(vcd);
			sc.sendChangeNotification(vcd);
		}
	}
	
	/**
	 * Send not-notified changes for each subscriber client 
	 */
	public void notifyAllChanges(){
		Set<Entry<Integer, LongConnectionClientSkeleton>> entrySet = lcMap.entrySet();
		for(Entry<Integer, LongConnectionClientSkeleton> item : entrySet){
			SubscriberClientSkeleton sc = (SubscriberClientSkeleton)  item.getValue();
			sc.sendAllChangeNotifications();
		}
	}
	
	public void setCaredKey(int clientId, String key){
		SubscriberClientSkeleton sc = (SubscriberClientSkeleton)  lcMap.get(clientId);
		if(sc == null)
			throw new RuntimeException("Cound not find subscriber client with id = "+ clientId); 
		sc.addCaredKey(key);
	}
}
