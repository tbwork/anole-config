package org.tbwork.anole.hub.server.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.impl.PublisherClientManager;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManager;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManager;

@Component("connectionRecycle") 
public class ConnectionRecycle {

	@Autowired
	private SubscriberClientManager scm;
	
	@Autowired
	private PublisherClientManager pcm;
	
	@Autowired
	private WorkerClientManager wcm;
	
	@Scheduled(fixedDelay = StaticConfiguration.PROMISE_PING_INTERVAL)
	public void run(){
		scm.promisePingAndScavenge();
		pcm.promisePingAndScavenge();
		wcm.promisePingAndScavenge();
	}
	
}
