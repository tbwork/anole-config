package org.tbwork.anole.hub.server.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.client.manager.impl.SubscriberClientManager;

@Component("connectionRecycle") 
public class ConnectionRecycle {

	@Autowired
	private SubscriberClientManager scm;
	
	@Scheduled(fixedDelay = StaticConfiguration.PING_PERIOD_SECOND)
	public void run(){
		scm.promisePingAndScavenge();
	}
	
}
