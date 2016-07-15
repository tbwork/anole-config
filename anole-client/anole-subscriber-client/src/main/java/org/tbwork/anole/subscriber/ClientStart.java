package org.tbwork.anole.subscriber;

import java.util.concurrent.TimeUnit;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.subscriber.client.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig; 
import org.tbwork.anole.subscriber.core.impl.AnoleSubscriberClasspathLoader; 

/**
 * Hello world!
 *
 */
public class ClientStart 
{
    public static void main( String[] args )
    { 
    	testAnole();
    }
    
    
    public static void testAnole(){
    	 
    	AnoleLoader anoleLoader = new AnoleSubscriberClasspathLoader();
    	anoleLoader.load();  
    	System.out.println(AnoleConfig.getProperty("pcs", "default")); 
    }
    
    public static void testLocalConfig(){
    	
    	AnoleLoader anoleLoader = new AnoleClasspathLoader();
    	anoleLoader.load();
    	System.out.println(AnoleConfig.getProperty("cs")); 
    }
}
