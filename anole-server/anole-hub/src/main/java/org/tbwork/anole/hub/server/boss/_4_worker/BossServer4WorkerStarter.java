package org.tbwork.anole.hub.server.boss._4_worker;

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
public class BossServer4WorkerStarter
{  
	private final static Logger logger = LoggerFactory.getLogger(BossServer4WorkerStarter.class);
	
	@Autowired
	private AnoleWorkerManagerBossServer anoleWorkerManagerBossServer;
	
	private static final String DEFAULT_PORTS = "54325,54326"; 
	 
	public void run()
    {   
    	int port = StringUtil.getPort("anole.server.boss.4worker.port", DEFAULT_PORTS); 
    	anoleWorkerManagerBossServer.start(port); 
    } 
}
