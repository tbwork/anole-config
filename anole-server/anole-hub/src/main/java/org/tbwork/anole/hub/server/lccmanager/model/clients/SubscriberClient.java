package org.tbwork.anole.hub.server.lccmanager.model.clients;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
 
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.W2CConfigChangeNotifyMessage;
import org.tbwork.anole.common.model.ConfigModifyDTO; 
import org.tbwork.anole.hub.server.util.ChannelHelper;
 
import lombok.Data; 
import io.netty.channel.socket.SocketChannel;

@Data
public class SubscriberClient extends LongConnectionClient{ 
	
	private Map<String,ConfigModifyDTO> unNotifiedChangeMap = new HashMap<String,ConfigModifyDTO>(); 
	private Set<String> caredKeySet = new HashSet<String>();
	
	public SubscriberClient(int token, SocketChannel socketChannel){
		super(token, socketChannel);
	}

	public void addNewChangeNotification(ConfigModifyDTO ccd){
		if(!caredKeySet.contains(ccd.getKey())) // this client is not care about this key
			return;
		ConfigModifyDTO old = unNotifiedChangeMap.get(ccd.getKey());
		if( old != null && old.getTimestamp() >= ccd.getTimestamp())
			return;
		unNotifiedChangeMap.put(ccd.getKey(), ccd);
	}
	
	public void sendAllChangeNotifications(){
		Set<Entry<String,ConfigModifyDTO>> entrySet = unNotifiedChangeMap.entrySet();
		for(Entry<String,ConfigModifyDTO> item : entrySet){
			W2CConfigChangeNotifyMessage message = new W2CConfigChangeNotifyMessage(item.getValue());
			ChannelHelper.sendMessage(this, message);
		}
	}
	
	public void ackChangeNotification(String key, long timestamp){ 
		ConfigModifyDTO changeNotificationItem =  unNotifiedChangeMap.get(key);
		if(changeNotificationItem!=null || changeNotificationItem.getTimestamp() == timestamp){
			unNotifiedChangeMap.remove(key);
		} 
	}
	 
}
