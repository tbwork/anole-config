package org.tbwork.anole.hub.server.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.impl.PublisherClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss;

@Component("connectionRecycle") 
public class ConnectionRecycle {

	@Autowired
	private SubscriberClientManagerForWorker scm;
	
	@Autowired
	private PublisherClientManagerForBoss pcm;
	
	@Autowired
	private WorkerClientManagerForBoss wcm;
	
	@Scheduled(fixedDelay = StaticConfiguration.PROMISE_PING_INTERVAL)
	public void run(){
		scm.promisePingAndScavenge("subscriber");
		pcm.promisePingAndScavenge("publisher");
		wcm.promisePingAndScavenge("worker");
	}
	
}
