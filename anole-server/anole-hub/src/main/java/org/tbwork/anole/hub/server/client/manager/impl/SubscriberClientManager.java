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
import org.tbwork.anole.common.message.c_2_s.PingMessage;
import org.tbwork.anole.common.message.s_2_c.PingAckMessage;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.client.manager.BaseClientManager; 
import org.tbwork.anole.hub.server.client.manager.model.BaseOperationRequest;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberClient;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberRegisterRequest;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberUnregisterRequest;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberValidateRequest;
import org.tbwork.anole.hub.server.util.ChannelHelper;

/**
 * SubscriberClientManager is all-in-one management tool
 * used for Anole Push Server to manage all subscriber clients.
 * This manager maintains a registry map which contains 
 * information of all clients with long connections.
 * The Subscribers(clients) do not need to worry about abuse
 * of connection due to the following features of SubscriberClientManager:<br>
 * <p><b>1.</b> A Ping thread periodically sends ping message to
 * all of the clients respectively, and if certain client failed to
 * response, its corresponding no_response_count would increase by one,
 * {@link SubscriberClient#addPingPromise()}.
 * <p><b>2.</b> A scavenger thread periodically clean bad clients whose
 * connection with server is disconnected ( valid = false or
 * no_response_count > MAX_NO_RESPONSE_COUNT ).
 * @author Tommy.Tang
 */
@Service("subscriberClientManager")
public class SubscriberClientManager implements BaseClientManager{

	private static final Logger logger = LoggerFactory.getLogger(SubscriberClientManager.class);
	
	public Map<Integer,SubscriberClient> subscriberMap = new ConcurrentHashMap<Integer,SubscriberClient>();
	
	public ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private int totalCnt;
	
	private int aliveCnt;
	
	public boolean validte(BaseOperationRequest request) {
		SubscriberValidateRequest svRequest = (SubscriberValidateRequest)request;
		SubscriberClient client = subscriberMap.get(svRequest.getClientId());
		if(client != null && svRequest.getToken() == client.getToken())
	       return true; 
		return false;
	}
 
	public void registerClient(BaseOperationRequest request) {
		SubscriberRegisterRequest srRequest =(SubscriberRegisterRequest)request;
		SubscriberClient client = new SubscriberClient(srRequest.getToken(), srRequest.getSocketChannel());
		subscriberMap.put(srRequest.getClientId(), client); 
	}

	public void unregisterClient(BaseOperationRequest request) {
		SubscriberUnregisterRequest suRequest =(SubscriberUnregisterRequest)request;
		subscriberMap.remove(suRequest.getClientId()); 
	}

	public void ackPing(int clientId){
		SubscriberClient client = subscriberMap.get(clientId);
		if(client != null){
			client.achievePingPromise();
			PingAckMessage pam = new PingAckMessage(); 
			pam.setIntervalTime(StaticConfiguration.PROMISE_PING_INTERVAL); 
			ChannelHelper.sendMessage(client, pam);
		} 
	}
	
	public void promisePingAndScavenge(){ 
		synchronized(subscriberMap){
				logger.info("[:)] Start to add ping promise and sweep bad clients."); 
				Set<Entry<Integer,SubscriberClient>> entrySet = subscriberMap.entrySet();
				int totalCnt = entrySet.size();
				int badCnt = 0;
				for(Entry<Integer,SubscriberClient> item: entrySet)
				{
					SubscriberClient client = item.getValue();
					if(client.maxPromiseCount()){
						subscriberMap.remove(item.getKey());
						badCnt ++;
					}
					else
						item.getValue().addPingPromise(); 
				} 
				logger.info("[:)] Adding ping promise and sweep bad clients done successfully! Scavenger report: total count of clients:{}, count of alive clients:{}, count of bad clients:{}", totalCnt, totalCnt-badCnt, badCnt);
		} 
	}
}
