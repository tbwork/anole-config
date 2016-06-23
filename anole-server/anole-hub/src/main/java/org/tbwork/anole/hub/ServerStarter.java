package org.tbwork.anole.hub;

import java.util.concurrent.TimeUnit;

import org.anole.infrastructure.dao.AnoleConfigItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tbwork.anole.hub.repository.ConfigRepository;
import org.tbwork.anole.hub.server.worker.subscriber.AnoleSubscriberServer;
import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;

/**
 * Yes, Anole goes here.
 */ 
public class ServerStarter
{  
	private final static Logger logger = LoggerFactory.getLogger(ServerStarter.class);
	
    @SuppressWarnings("resource")
	public static void main( String[] args ) throws InterruptedException
    { 
    	logger.info("[:)] Anole server is starting...");
    	AnoleLoader al = new AnoleClasspathLoader();
    	al.load();  
    	
    	ApplicationContext context = new ClassPathXmlApplicationContext(
        		"spring/spring-context.xml",
        		"classpath*:spring/spring-database.xml"
        		);
        
        AnoleSubscriberServer apServer = (AnoleSubscriberServer) context.getBean("anolePushServer");
        if(apServer!=null)
        {
        	apServer.start(AnoleLocalConfig.getIntProperty("anole.server.push.port", 54321)); 
        }   
        logger.info("[:)] Anole server started successfully.");
        TimeUnit.SECONDS.sleep(10);
        apServer.close();
    }
}
