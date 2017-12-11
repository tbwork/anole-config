package org.tbwork.anole.hub.server.boss._4_subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.util.StringUtil;
 

/**
 * Yes, Anole goes here.
 */ 
@Service
public class BossServer4SubscriberStarter
{  
	private final static Logger logger = LoggerFactory.getLogger(BossServer4SubscriberStarter.class);
	
	@Autowired
	private AnoleSubscriberManagerBossServer anoleSubscriberManagerBossServer;
	
	private static final String DEFAULT_PORTS = "54323,54324"; 
	 
	public void run()
    {   
    	int port = StringUtil.getPort("anole.server.boss.4subscriber.port", DEFAULT_PORTS); 
    	anoleSubscriberManagerBossServer.start(port);  
    } 
}
