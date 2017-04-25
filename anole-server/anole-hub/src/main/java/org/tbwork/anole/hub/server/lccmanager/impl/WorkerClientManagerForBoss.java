package org.tbwork.anole.hub.server.lccmanager.impl;
 
 
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.enums.ClientType;  
import org.tbwork.anole.common.model.ValueChangeDTO;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClientSkeleton; 
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClientSkeleton;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClientSkeleton.CustomerClient; 
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.util.ClientEntropyUtil;
 
/**
 * Worker manager used by boss server.
 * @author tommy.tang
 */
@Service("workerClientManager")
public class WorkerClientManagerForBoss extends LongConnectionClientManager {
 
	private static final Logger logger = LoggerFactory.getLogger(WorkerClientManagerForBoss.class); 
	private final ExecutorService fixedPool = Executors.newFixedThreadPool(StaticConfiguration.WORKER_CLIENT_OPS_THREAD_POOL_SIZE);
	
	@Override
	protected LongConnectionClientSkeleton createClient(int token, RegisterRequest registerRequest) { 
		return new WorkerClientSkeleton(token, registerRequest.getSocketChannel());
	} 
 
	@Override
	public void unregisterClient(UnregisterRequest request) { 
		WorkerClientSkeleton wc =  (WorkerClientSkeleton) lcMap.get(request.getClientId());
		if(wc != null){ 
			super.unregisterClient(request);
			String identity = wc.getIdentity();
			String ip = wc.getSocketChannel().remoteAddress().getAddress().getHostAddress(); 
		} 
	} 
	
	public void updateStatus(int clientId, int subscriberClientCount, int weight){
		WorkerClientSkeleton wc = (WorkerClientSkeleton)  lcMap.get(clientId);
		 if(wc != null){ 
			 wc.setSubscriberClientCount(subscriberClientCount);
			 wc.setWeight(weight);
		 } 
	}
	
	public WorkerClientSkeleton selectBestWorkerForSubscriber(){
		return ClientEntropyUtil.selectBestWorker(ClientType.SUBSCRIBER, lcMap);
	}
	  
	public void setRegisterResult(int clientId, int resultClientId, int resultToken, String resultIp, int resultPort, ClientType resultClientType){
		WorkerClientSkeleton wc =  (WorkerClientSkeleton) lcMap.get(clientId);
		if(wc == null)
			logger.error("Set register result failed: worker client (id = {}) is not found", clientId);
		else{
			if(wc.isProcessing() && !wc.isGiveup()){
				synchronized(wc){
					if(wc.isProcessing() && !wc.isGiveup()){
						CustomerClient cc = new CustomerClient(resultClientId, resultToken, resultPort, resultIp);
						//if(resultClientType == ClientType.SUBSCRIBER) 
						wc.setSubscriber(cc);
						wc.setProcessing(false);
						wc.notifyAll();
						return;
					} 
				}
			} 
			wc.setSubscriber(null);
		}
	}
	
	public <T> Future<T> executeThread(Callable<T> task){ 
		return fixedPool.submit(task);
	}
	
	public void ackChangeNotify(int clientId, String key, long timestamp){
		WorkerClientSkeleton wc = (WorkerClientSkeleton)  lcMap.get(clientId);
		wc.ackChangeNotification(key, timestamp);
	} 
	
	public void notifyChange(ValueChangeDTO vcd){
		Set<Entry<Integer, LongConnectionClientSkeleton>> entrySet = lcMap.entrySet();
		for(Entry<Integer, LongConnectionClientSkeleton> item : entrySet){
			WorkerClientSkeleton wc = (WorkerClientSkeleton)  item.getValue();
			wc.addNewChangeNotification(vcd);
			wc.sendChangeNotification(vcd);
		} 
	}
	
	/**
	 * Send not-notified changes for each worker client
	 */
	public void notifyAllChanges(){
		Set<Entry<Integer, LongConnectionClientSkeleton>> entrySet = lcMap.entrySet();
		for(Entry<Integer, LongConnectionClientSkeleton> item : entrySet){
			WorkerClientSkeleton wc = (WorkerClientSkeleton)  item.getValue();
			wc.sendAllChangeNotifications();
		}
	}

}
