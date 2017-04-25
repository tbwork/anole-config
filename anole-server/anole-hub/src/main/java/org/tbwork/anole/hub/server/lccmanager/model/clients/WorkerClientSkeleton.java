package org.tbwork.anole.hub.server.lccmanager.model.clients;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tbwork.anole.common.message.s_2_c.boss._2_worker.B2WConfigChangeNotifyMessage;  
import org.tbwork.anole.common.model.ValueChangeDTO; 
import org.tbwork.anole.hub.server.util.ChannelHelper;
 
import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor; 
import io.netty.channel.socket.SocketChannel;

/**
 * Used by boss server, a worker client is also a server  
 * provides subscribing services.
 * @author tommy.tang
 */
@Data
public class WorkerClientSkeleton extends LongConnectionClientSkeleton{
 
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
	private Map<String,ValueChangeDTO> unNotifiedChangeMap = new HashMap<String,ValueChangeDTO>(); 
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerClient{
		private int clientId;
		private int token; 
		private int port;
		private String lanIp;
	}
	
	public WorkerClientSkeleton(int token, SocketChannel socketChannel){
		super(token, socketChannel);  
		this.subscriberClientCount = 0;
		this.weight = 10;
		processing = false;
	}
	
	public void addNewChangeNotification(ValueChangeDTO vcd){
		ValueChangeDTO old = unNotifiedChangeMap.get(vcd.getKey());
		if(old != null && old.getTimestamp() >= vcd.getTimestamp())
			return;
		unNotifiedChangeMap.put(vcd.getKey(), vcd);
	} 
	
	public void sendAllChangeNotifications(){
		Set<Entry<String,ValueChangeDTO>> entrySet = unNotifiedChangeMap.entrySet();
		for(Entry<String,ValueChangeDTO> item : entrySet){
			B2WConfigChangeNotifyMessage message = new B2WConfigChangeNotifyMessage(item.getValue());
			ChannelHelper.sendMessage(this, message);
		}
	}
	
	public void sendChangeNotification(ValueChangeDTO vcd){
		B2WConfigChangeNotifyMessage message = new B2WConfigChangeNotifyMessage(vcd);
		ChannelHelper.sendMessage(this, message); 
	}
	
	public void ackChangeNotification(String key, long timestamp){ 
		ValueChangeDTO changeNotificationItem =  unNotifiedChangeMap.get(key);
		if(changeNotificationItem!=null || changeNotificationItem.getTimestamp() == timestamp){
			unNotifiedChangeMap.remove(key);
		} 
	}
	 
}
