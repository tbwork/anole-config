package org.tbwork.anole.hub.server.boss._4_publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.util.StringUtil;

/**
 * Yes, Anole goes here.
 */ 
@Service
public class BossServer4PublisherStarter
{  
	private final static Logger logger = LoggerFactory.getLogger(BossServer4PublisherStarter.class);
	
	@Autowired
	private AnolePublisherManagerBossServer anolePublisherManagerBossServer;
	
	private static final String DEFAULT_PORTS = "54321,54322"; 
	 
	public void run()
    {  
    	int port = StringUtil.getPort("anole.server.boss.4publisher.port", DEFAULT_PORTS); 
    	anolePublisherManagerBossServer.start(port);  
    }
     
}
