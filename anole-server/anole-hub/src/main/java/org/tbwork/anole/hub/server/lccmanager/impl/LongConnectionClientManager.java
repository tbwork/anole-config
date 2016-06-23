package org.tbwork.anole.hub.server.lccmanager.impl;

import io.netty.channel.socket.SocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.message.s_2_c.PingAckMessage;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.ILongConnectionClientManager;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClient;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.ValidateRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.params.IRegisterParameter;
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
	
	public Map<Integer, LongConnectionClient> lcMap = new ConcurrentHashMap<Integer, LongConnectionClient>();
	
	private int totalCnt;
	
	private int aliveCnt;
	
	@Override
	public boolean validate(ValidateRequest request) { 
		LongConnectionClient client = lcMap.get(request.getClientId());
		if(client != null && request.getToken() == client.getToken())
	       return true; 
		return false;
	}
 
	protected abstract LongConnectionClient createClient(int token, RegisterRequest registerRequest);
	
	protected abstract boolean validate(SocketChannel socketChannel, IRegisterParameter registerParameter);
	 
	@Override
	public RegisterResult registerClient(RegisterRequest request) {  
		if(validate(request.getSocketChannel(), request.getRegisterParameter())){ 
			  ClientInfo clientInfo =  ClientInfoGenerator.generate(request.getClientType());  
			  LongConnectionClient client = createClient(clientInfo.getToken(), request);
			  lcMap.put(clientInfo.getClientId(), client); 
			  return new RegisterResult(clientInfo.getToken(), clientInfo.getClientId(), true);
		}
		else
			return new RegisterResult(0, 0, false);  
	}
	
	
	@Override
	public void unregisterClient(UnregisterRequest request) { 
		lcMap.remove(request.getClientId()); 
	}

	@Override
	public void ackPing(int clientId){
		LongConnectionClient client = lcMap.get(clientId);
		if(client != null){
			client.achievePingPromise();
			PingAckMessage pam = new PingAckMessage(); 
			pam.setIntervalTime(StaticConfiguration.PROMISE_PING_INTERVAL); 
			ChannelHelper.sendMessage(client, pam);
		} 
	}
	
	@Override
	public void promisePingAndScavenge(){ 
		synchronized(lcMap){
				logger.info("[:)] Start to add ping promise and sweep bad clients."); 
				Set<Entry<Integer,LongConnectionClient>> entrySet = lcMap.entrySet();
				int totalCnt = entrySet.size();
				int badCnt = 0;
				for(Entry<Integer,LongConnectionClient> item: entrySet)
				{
					LongConnectionClient client = item.getValue();
					if(client.maxPromiseCount()){
						lcMap.remove(item.getKey());
						badCnt ++;
					}
					else
						item.getValue().addPingPromise(); 
				} 
				logger.info("[:)] Adding ping promise and sweep bad clients done successfully! Scavenger report: total count of clients:{}, count of alive clients:{}, count of bad clients:{}", totalCnt, totalCnt-badCnt, badCnt);
		} 
	}
	
}
