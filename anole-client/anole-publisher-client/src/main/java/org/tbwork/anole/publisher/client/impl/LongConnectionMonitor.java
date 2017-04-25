package org.tbwork.anole.publisher.client.impl;

import java.util.Timer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.message.c_2_s.PingMessage;
import org.tbwork.anole.publisher.client.AnoleClientConfig;
import org.tbwork.anole.publisher.client.ConnectionMonitor;
import org.tbwork.anole.publisher.client.IAnolePublisherClient;
import org.tbwork.anole.publisher.client.PingTask;
import org.tbwork.anole.publisher.client.StaticClientConfig; 

public class LongConnectionMonitor implements ConnectionMonitor{

	private static final Logger logger = LoggerFactory.getLogger(LongConnectionMonitor.class);
	private static final LongConnectionMonitor lcMonitor = new LongConnectionMonitor();
	private IAnolePublisherClient client = AnolePublisherClient.instance();
	private Timer timer = null;
	private LongConnectionMonitor() { }
	private volatile boolean started = false;
	
	public static LongConnectionMonitor instance(){
		return lcMonitor;
	}
	
	@Override
	public void start() {  
		if(!started){ // no need to be thread-safe
			timer = new Timer();
			timer.schedule(new PingTask(), StaticClientConfig.PING_DELAY, StaticClientConfig.PING_INTERVAL);
			started = true;
		} 
	}

	@Override
	public void stop() { 
		started = false;
		timer.cancel();
		timer = null;
	}

	@Override
	public void restart() {
		stop();
		start();
	}
	 
	
}
