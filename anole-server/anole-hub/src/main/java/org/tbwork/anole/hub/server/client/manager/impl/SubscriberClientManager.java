package org.tbwork.anole.hub.server.client.manager.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service; 
import org.tbwork.anole.common.message.s_2_c.PingMessage;
import org.tbwork.anole.hub.server.client.manager.BaseClientManager; 
import org.tbwork.anole.hub.server.client.manager.StaticConfiguration;
import org.tbwork.anole.hub.server.client.manager.model.BaseOperationRequest;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberClient;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberRegisterRequest;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberUnregisterRequest;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberValidateRequest;

/**
 * SubscriberClientManager is all-in-one management tool
 * used for Anole Push Server to manage all subscriber clients.
 * This manager maintains a registry map which contains 
 * information of all clients with long connections.
 * The Subscribers(clients) do not need to worry about abuse
 * of connection due to the following features of SubscriberClientManager:<br>
 * <b>1.</b> A Ping thread periodically sends ping message to
 * all of the clients respectively, and if certain client failed to
 * response, its corresponding no_response_count would increase by one,
 * {@link SubscriberClient#increaseNoResponseCount()}.<br>
 * <b>2.</b> A scavenger thread periodically clean bad clients whose
 * connection with server is disconnected ( valid = false or
 * no_response_count > MAX_NO_RESPONSE_COUNT ).
 * @author Tommy.Tang
 */
@Service("subscriberClientManager")
public class SubscriberClientManager implements BaseClientManager{

	private static final Logger logger = LoggerFactory.getLogger(SubscriberClientManager.class);
	
	public Map<Integer,SubscriberClient> subscriberMap = new ConcurrentHashMap<Integer,SubscriberClient>();
	
	public ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	public volatile int scavenger_count_down = StaticConfiguration.SCAVENGER_PERIOD_BY_TIMES_OF_PING_PERIOD_SECOND;
	
	public boolean validte(BaseOperationRequest request) {
		SubscriberValidateRequest svRequest = (SubscriberValidateRequest)request;
		SubscriberClient client = subscriberMap.get(svRequest.getClientId());
		if(client != null && svRequest.getToken() == client.getToken())
	       return true; 
		return false;
	}
 
	public void registerClient(BaseOperationRequest request) {
		SubscriberRegisterRequest srRequest =(SubscriberRegisterRequest)request;
		SubscriberClient client = new SubscriberClient(srRequest.getClientId(), srRequest.getSocketChannel());
		subscriberMap.put(srRequest.getClientId(), client); 
	}

	public void unregisterClient(BaseOperationRequest request) {
		SubscriberUnregisterRequest suRequest =(SubscriberUnregisterRequest)request;
		SubscriberClient client = subscriberMap.get(suRequest.getClientId()); 
		if(client != null)  client.setValid(false);  
	}

	public void pincAck(int clientId){
		SubscriberClient client = subscriberMap.get(clientId);
		if(client != null)
			client.decreaseNoResponseCount();
	}
	public void pingAndScavenge(){ 
		synchronized(subscriberMap){
			if(scavenger_count_down > 0) // ping
			{
				logger.info("[:)] Loop of ping for all clients starts! ");
				Set<Entry<Integer,SubscriberClient>> entrySet = subscriberMap.entrySet();
				for(Entry<Integer,SubscriberClient> item: entrySet)
				{
					Integer key = item.getKey();
					SubscriberClient client = subscriberMap.get(key);
					ping(client);
				}
				scavenger_count_down --;
				logger.info("[:)] Loop of ping for all clients done successfully! ");
			}
			else // Time for scavenger to clean connections.
			{
				logger.info("[:)] Cleaning bad clients starts! ");
				Set<Entry<Integer,SubscriberClient>> entrySet = subscriberMap.entrySet();
				int totalCnt = entrySet.size();
				int badCnt = 0;
				for(Entry<Integer,SubscriberClient> item: entrySet)
				{
					Integer key = item.getKey();
					SubscriberClient client = subscriberMap.get(key);
					if(!client.isValid() || client.maxNoResponsecount())
					{
						subscriberMap.remove(key);
						badCnt ++;
					}
				}
				scavenger_count_down = StaticConfiguration.SCAVENGER_PERIOD_BY_TIMES_OF_PING_PERIOD_SECOND;
				logger.info("[:)] Cleaning bad clients done successfully, total count of clients:{}, count of alive clients:{}, count of bad clients:{}", totalCnt, totalCnt-badCnt, badCnt);
			}
				
		} 
	}
	
    private void ping(final SubscriberClient client)
    { 
    	if(client.maxNoResponsecount()) client.setValid(false); 
    	if(!client.isValid())           return;
    		
    	ChannelFuture f = client.getSocketChannel().writeAndFlush(new PingMessage());
    	f.addListener(new ChannelFutureListener(){

			public void operationComplete(ChannelFuture future)
					throws Exception {
				 client.increaseNoResponseCount(); //increase by one.
			}
    	}); 
    }

}
