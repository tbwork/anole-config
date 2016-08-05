package org.tbwork.anole.hub.server.boss._4_subscriber;

import java.util.concurrent.TimeUnit;

import org.anole.infrastructure.dao.AnoleConfigItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.repository.ConfigRepository;
import org.tbwork.anole.hub.server.worker.subscriber.AnoleSubscriberManagerWorkerServer;
import org.tbwork.anole.hub.util.StringUtil;
import org.tbwork.anole.hub.util.SystemUtil;
import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
 

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
