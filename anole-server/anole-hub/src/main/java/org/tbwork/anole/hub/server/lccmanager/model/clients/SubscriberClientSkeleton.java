package org.tbwork.anole.hub.server.lccmanager.model.clients;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
 
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.W2SConfigChangeNotifyMessage;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.common.model.ValueChangeDTO;
import org.tbwork.anole.hub.server.util.ChannelHelper;
 
import lombok.Data; 
import io.netty.channel.socket.SocketChannel;

@Data
public class SubscriberClientSkeleton extends LongConnectionClientSkeleton{ 
	
	private String enviroment;
	private Map<String,ValueChangeDTO> unNotifiedChangeMap = new HashMap<String,ValueChangeDTO>(); 
	private Set<String> caredKeySet = new HashSet<String>();
	
	public SubscriberClientSkeleton(int token, SocketChannel socketChannel, String environment){
		super(token, socketChannel);
		this.enviroment = environment;
	}

	public void addNewChangeNotification(ValueChangeDTO vcd){
		if(!enviroment.equals(vcd.getEnv())) // this client is not care about this environment
			return ;
		if(!caredKeySet.contains(vcd.getKey())) // this client is not care about this key
			return;
		ValueChangeDTO old = unNotifiedChangeMap.get(vcd.getKey());
		if( old != null && old.getTimestamp() >= vcd.getTimestamp())
			return; 
		unNotifiedChangeMap.put(vcd.getKey(), vcd);
	}
	
	public void sendAllChangeNotifications(){
		Set<Entry<String,ValueChangeDTO>> entrySet = unNotifiedChangeMap.entrySet();
		for(Entry<String,ValueChangeDTO> item : entrySet){
			W2SConfigChangeNotifyMessage message = new W2SConfigChangeNotifyMessage(item.getValue());
			ChannelHelper.sendMessage(this, message);
		}
	}
	
	public void sendChangeNotification(ValueChangeDTO vcd){
		if(!enviroment.equals(vcd.getEnv())) // this client is not care about this environment
			return ;
		if(!caredKeySet.contains(vcd.getKey())) // this client is not care about this key
			return;
		W2SConfigChangeNotifyMessage message = new W2SConfigChangeNotifyMessage(vcd);
		ChannelHelper.sendMessage(this, message); 
	}
	
	public void ackChangeNotification(String key, long timestamp){ 
		ValueChangeDTO changeNotificationItem =  unNotifiedChangeMap.get(key);
		if(changeNotificationItem!=null || changeNotificationItem.getTimestamp() == timestamp){
			unNotifiedChangeMap.remove(key);
		} 
	}
	
	public void addCaredKey(String key){
		caredKeySet.add(key);
	}
}
