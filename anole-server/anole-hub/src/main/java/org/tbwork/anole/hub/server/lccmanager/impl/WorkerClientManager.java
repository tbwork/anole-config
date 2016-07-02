package org.tbwork.anole.hub.server.lccmanager.impl;
 

import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClient.CustomerClient;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterParameter;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.util.ClientEntropyUtil;

import io.netty.channel.socket.SocketChannel;

@Service("workerClientManager")
public class WorkerClientManager extends LongConnectionClientManager {
 
	private static final Logger logger = LoggerFactory.getLogger(WorkerClientManager.class); 
	private final ExecutorService fixedPool = Executors.newFixedThreadPool(StaticConfiguration.WORKER_CLIENT_OPS_THREAD_POOL_SIZE);
	
	@Override
	protected LongConnectionClient createClient(int token, RegisterRequest registerRequest) { 
		return new WorkerClient(token, registerRequest.getSocketChannel());
	} 
 
	@Override
	public void unregisterClient(UnregisterRequest request) { 
		WorkerClient wc =  (WorkerClient) lcMap.get(request.getClientId());
		if(wc != null){ 
			super.unregisterClient(request);
			String identity = wc.getIdentity();
			String ip = wc.getSocketChannel().remoteAddress().getAddress().getHostAddress();
			//delete from anole_hub where Type = 2  and address = #ip and identity = #identity;
		} 
	} 
	
	public void updateStatus(int clientId, int publisherClientCount, int subscriberClientCount, int weight){
		 WorkerClient wc = (WorkerClient)  lcMap.get(clientId);
		 if(wc != null){
			 wc.setPublisherClientCount(publisherClientCount);
			 wc.setSubscriberClientCount(subscriberClientCount);
			 wc.setWeight(weight);
		 } 
	}
	
	public WorkerClient selectBestWorkerForSubscriber(){
		return ClientEntropyUtil.selectBestWorker(ClientType.SUBSCRIBER, lcMap);
	}
	

	
	public WorkerClient selectBestWorkerForPublisher(){
		return ClientEntropyUtil.selectBestWorker(ClientType.PUBLISHER, lcMap);
	}
	  
	public void setRegisterResult(int clientId, int resultClientId, int resultToken, int resultPort, ClientType resultClientType){
		WorkerClient wc =  (WorkerClient) lcMap.get(clientId);
		if(wc == null)
			logger.error("Set register result failed: worker client (id = {}) is not found", clientId);
		else{
			if(wc.isProcessing() && !wc.isGiveup()){
				CustomerClient cc = new CustomerClient(resultClientId, resultToken, resultPort);
				if(resultClientType == ClientType.PUBLISHER)
					wc.setPublisher(cc);
				else
					wc.setSubscriber(cc);
				wc.setProcessing(false);
				wc.notifyAll();
			}
			wc.setPublisher(null);
			wc.setSubscriber(null);
		}
	}
	
	public <T> Future<T> executeThread(Callable<T> task){ 
		return fixedPool.submit(task);
	}
}
