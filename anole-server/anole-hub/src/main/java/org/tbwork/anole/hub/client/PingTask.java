package org.tbwork.anole.hub.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.message.c_2_s.PingMessage;
import org.tbwork.anole.common.message.c_2_s.worker_2_boss.WorkerPingMessage;
import org.tbwork.anole.hub.client.worker.AnoleWorkerClient;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss;
import org.tbwork.anole.hub.server.worker.subscriber.AnoleSubscriberManagerWorkerServer;
import org.tbwork.anole.loader.core.Anole;

public class PingTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(PingTask.class);
	
	public PingTask(IAnoleWorkerClient client, WorkerClientManagerForBoss workerClientManagerForBoss){
		this.client = client;
		this.workerClientManagerForBoss = workerClientManagerForBoss;
	}
	 
	private IAnoleWorkerClient client;
	 
	private WorkerClientManagerForBoss workerClientManagerForBoss;
	
	@Override
	public void run() { 
		try{ 
			 ping(); 
		}
		catch(Exception e){
			logger.error("Ping failed. Details: {}", e.getMessage());
		} 
	}

	private void ping(){   
		if(!client.canPing())
		    client.setConnected(false); 
		if(!client.isConnected())
			client.connect();
		WorkerPingMessage workerPingMessage = new WorkerPingMessage();
		workerPingMessage.setWeight(Anole.getIntProperty("worker.weight", WorkerClientConfig.WEIGHT));
		workerPingMessage.setSubscriberClientCount(workerClientManagerForBoss.getClientCount());
		if(logger.isInfoEnabled()){
			client.sendMessageWithListeners(workerPingMessage, 
				new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						logger.debug("[:)] Ping message is sent successfully.");
					}
				}
			);
		}
		else
			client.sendMessage(new PingMessage()); 
		client.addPingCount();
	}
}
