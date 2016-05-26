package org.tbwork.anole.hub;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tbwork.anole.hub.server.push.AnolePushServer;
import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;

/**
 * Hello world!
 *
 */ 
public class ServerStarter
{ 
    public static void main( String[] args )
    { 
    	AnoleLoader al = new AnoleClasspathLoader();
    	al.load(); 
    	
    	ApplicationContext context = new ClassPathXmlApplicationContext(
        		"spring/spring-context.xml",
        		"classpath*:spring/spring-database.xml"
        		);
        
        AnolePushServer apServer = (AnolePushServer) context.getBean("anolePushServer");
        if(apServer!=null)
        {
        	//apServer.start(8080);
        	System.out.println(apServer.getName());
        } 
        
    }
}
