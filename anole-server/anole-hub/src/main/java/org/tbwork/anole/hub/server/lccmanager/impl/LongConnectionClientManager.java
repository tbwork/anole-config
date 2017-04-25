package org.tbwork.anole.hub.server.lccmanager.impl;

import io.netty.channel.socket.SocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.s_2_c.PingAckMessage;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.ILongConnectionClientManager;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClientSkeleton;
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClientSkeleton;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterParameter;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.ValidateRequest;
import org.tbwork.anole.hub.server.lccmanager.model.response.RegisterResult;
import org.tbwork.anole.hub.server.util.ChannelHelper;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator.ClientInfo; 

/**
 * LongConnectionClientManager provides basic implementations of 
 * ILongConnectionClientManager to manage long-connection clients.
 * This manager maintains a registry table (actually a map) which  
 * contains information of all long-connection Anole clients.
 * The long-connection clients do not need to worry about abuse
 * of connection due to the following features:<br>
 * <p><b>1.</b> A heart beat mechanism is used to detect the bad
 * connections via periodically receiving ping message from clients.
 * <p><b>2.</b> A scavenger thread periodically clean bad clients whose
 * connection with server is disconnected ( valid = false or
 * no_response_count > MAX_NO_RESPONSE_COUNT ).
 * @author Tommy.Tang
 */
public abstract class LongConnectionClientManager implements ILongConnectionClientManager{

	private static final Logger logger = LoggerFactory.getLogger(LongConnectionClientManager.class);
	
	public Map<Integer, LongConnectionClientSkeleton> lcMap = new ConcurrentHashMap<Integer, LongConnectionClientSkeleton>(); 
	 
	private int validClientCount;
	
	@Override
	public boolean validate(ValidateRequest request) { 
		LongConnectionClientSkeleton client = lcMap.get(request.getClientId());
		if(client != null && request.getToken() == client.getToken())
	       return true; 
		return false;
	}
 
	/**
	 * Create a client using input registerRequest.
	 */
	protected abstract LongConnectionClientSkeleton createClient(int token, RegisterRequest registerRequest);
	 
	@Override
	public RegisterResult registerClient(RegisterRequest request) {  
		RegisterParameter rp = request.getRegisterParameter();  
		ClientInfo clientInfo =  ClientInfoGenerator.generate(request.getClientType());  
		LongConnectionClientSkeleton client = createClient(clientInfo.getToken(), request);
		lcMap.put(clientInfo.getClientId(), client); 
		validClientCount ++;
		return new RegisterResult(clientInfo.getToken(), clientInfo.getClientId(), true);  
	}
	
	@Override
	public void unregisterClient(UnregisterRequest request) { 
		unRegisterClient(request.getClientId());
	}
	
	private void unRegisterClient(int clientId){
		validClientCount --;
		lcMap.remove(clientId); 
	}

	@Override
	public void ackPing(int clientId){
		LongConnectionClientSkeleton client = lcMap.get(clientId);
		if(client != null){
			client.achievePingPromise();
			PingAckMessage pam = new PingAckMessage(); 
			pam.setIntervalTime(StaticConfiguration.PROMISE_PING_INTERVAL); 
			ChannelHelper.sendMessage(client, pam);
		} 
	}
	
	@Override
	public void promisePingAndScavenge(String clientName){ 
		synchronized(lcMap){
				logger.debug("[:)] Start to add ping promise and sweep bad {} clients.", clientName); 
				Set<Entry<Integer,LongConnectionClientSkeleton>> entrySet = lcMap.entrySet();
				int totalCnt = entrySet.size();
				int badCnt = 0;
				for(Entry<Integer,LongConnectionClientSkeleton> item: entrySet)
				{
					LongConnectionClientSkeleton client = item.getValue();
					if(client.maxPromiseCount()){
						unRegisterClient(item.getKey());
						badCnt ++;
					}
					else
						item.getValue().addPingPromise(); 
				} 
				logger.debug("[:)] Adding ping promise and sweep bad {} clients done successfully! Scavenger report: total count of clients:{}, count of alive clients:{}, count of bad clients:{}", clientName, totalCnt, totalCnt-badCnt, badCnt);
		} 
	}
	
	@Override
	public int getClientCount() { 
		return validClientCount;
	}
	 
}
