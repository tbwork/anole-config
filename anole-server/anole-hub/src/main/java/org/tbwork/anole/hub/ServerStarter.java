package org.tbwork.anole.hub;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tbwork.anole.hub.server.push.AnolePushServer;

/**
 * Hello world!
 *
 */ 
public class ServerStarter
{
	
	
    public static void main( String[] args )
    {
        ApplicationContext context = new ClassPathXmlApplicationContext(
        		"spring/spring-context.xml"
        		);
        AnolePushServer apServer = (AnolePushServer) context.getBean("anolePushServer");
        if(apServer!=null)
        {
        	apServer.start(8080);
        }
        
    }
}
