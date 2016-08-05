package org.tbwork.anole.hub.server.worker.subscriber;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.client.worker.AnoleWorkerClient;
import org.tbwork.anole.hub.server.worker.subscriber.AnoleSubscriberManagerWorkerServer; 
import org.tbwork.anole.hub.util.SystemUtil; 
 

/**
 * Yes, Anole goes here.
 */ 
@Service
public class WorkerServer4SubscriberStarter
{  
	private final static Logger logger = LoggerFactory.getLogger(WorkerServer4SubscriberStarter.class); 
	@Autowired
	private AnoleSubscriberManagerWorkerServer anoleSubscriberManagerWorkerServer; 
	
	@Autowired
	private AnoleWorkerClient worker;
	public void run()
    {  
		worker.connect();
    	int port = SystemUtil.getOneValidPort();
        anoleSubscriberManagerWorkerServer.start(port);
    } 
}
