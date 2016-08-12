package org.tbwork.anole.hub.client.impl;

import java.util.Timer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.message.c_2_s.PingMessage;
import org.tbwork.anole.hub.client.ConnectionMonitor;
import org.tbwork.anole.hub.client.IAnoleWorkerClient;
import org.tbwork.anole.hub.client.PingTask;
import org.tbwork.anole.hub.client.WorkerClientConfig;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss; 

@Component("workerLongConnectionMonitor")
public class LongConnectionMonitor implements ConnectionMonitor{

	private static final Logger logger = LoggerFactory.getLogger(LongConnectionMonitor.class); 
	private Timer timer = null;  
	@Autowired
	private IAnoleWorkerClient client;
	@Autowired
	private WorkerClientManagerForBoss workerClientManagerForBoss;
	
	@Override
	public void start() { 
		timer = new Timer();
		timer.schedule(new PingTask(client, workerClientManagerForBoss), WorkerClientConfig.PING_DELAY, WorkerClientConfig.PING_INTERVAL);
	}

	@Override
	public void stop() { 
		timer.cancel();
		timer = null;
	}

	@Override
	public void restart() {
		stop();
		start();
	}
	 
	
}
