package org.tbwork.anole.hub.server.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tbwork.anole.hub.StaticConfiguration;
import org.tbwork.anole.hub.server.lccmanager.impl.PublisherClientManagerForBoss;
import org.tbwork.anole.hub.server.lccmanager.impl.SubscriberClientManagerForWorker;
import org.tbwork.anole.hub.server.lccmanager.impl.WorkerClientManagerForBoss;

@Component("changeNotifyJob") 
public class ChangeNotifyJob {

	@Autowired
	private SubscriberClientManagerForWorker scm;
	
	@Autowired
	private PublisherClientManagerForBoss pcm;
	
	@Autowired
	private WorkerClientManagerForBoss wcm;
	
	@Scheduled(fixedDelay = StaticConfiguration.CHANGE_NOTIFY_INTERVAL)
	public void run(){
		scm.notifyAllChanges();
		wcm.notifyAllChanges();
	}
}
