package org.tbwork.anole.hub.server.lccmanager.model.clients;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tbwork.anole.common.message.s_2_c.boss._2_worker.B2WConfigChangeNotifyMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.W2CConfigChangeNotifyMessage;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.util.ChannelHelper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.netty.channel.socket.SocketChannel;

/**
 * A worker client is also a server provides publishing and 
 * subscribing services.
 * @author tommy.tang
 */
@Data
public class WorkerClient extends LongConnectionClient{

	/**
	 * The count of publisher clients in the worker server.
	 */
	private int publisherClientCount;
	
	/**
	 * The count of subscriber clients in the worker server.
	 */
	private int subscriberClientCount;
	
	
	/**
	 * The weight of the worker server.
	 */
	private int weight;
	
	/**
	 * The identity of the worker server.
	 */
	private String identity;
	
	private volatile boolean processing;
	
	private volatile boolean giveup; 
	
	private volatile CustomerClient subscriber;
	 
	private Map<String,ConfigModifyDTO> unNotifiedChangeMap = new HashMap<String,ConfigModifyDTO>(); 
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerClient{
		private int clientId;
		private int token; 
		private int port;
	}
	
	public WorkerClient(int token, SocketChannel socketChannel){
		super(token, socketChannel); 
		this.publisherClientCount = 0 ;
		this.subscriberClientCount = 0;
		this.weight = 10;
		processing = false;
	}
	
	public void addNewChangeNotification(ConfigModifyDTO ccd){
		ConfigModifyDTO old = unNotifiedChangeMap.get(ccd.getKey());
		if(old != null && old.getTimestamp() >= ccd.getTimestamp())
			return;
		unNotifiedChangeMap.put(ccd.getKey(), ccd);
	}
	
	public void sendAllChangeNotifications(){
		Set<Entry<String,ConfigModifyDTO>> entrySet = unNotifiedChangeMap.entrySet();
		for(Entry<String,ConfigModifyDTO> item : entrySet){
			B2WConfigChangeNotifyMessage message = new B2WConfigChangeNotifyMessage(item.getValue());
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
