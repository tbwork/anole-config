package org.tbwork.anole.hub.server.push;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.hub.server.client.manager.StaticConfiguration;
import org.tbwork.anole.hub.server.client.manager.impl.SubscriberClientManager;

@Component("scavenger") 
public class ScheduleJob {

	@Autowired
	private SubscriberClientManager scm;
	
	@Scheduled(fixedDelay = StaticConfiguration.PING_PERIOD_SECOND)
	public void run(){
		scm.pingAndScavenge();
	}
	
}
