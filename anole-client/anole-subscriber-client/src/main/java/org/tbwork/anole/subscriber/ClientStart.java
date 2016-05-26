package org.tbwork.anole.subscriber;

import java.util.concurrent.TimeUnit;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig;
import org.tbwork.anole.subscriber.core.AnoleSubscriberLoader;
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
    	 
    	AnoleSubscriberLoader anoleLoader = new AnoleSubscriberClasspathLoader();
    	anoleLoader.load(); 
    	
    	try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    	System.out.println(AnoleConfig.getProperty("1", "default"));
//    	System.out.println(AnoleConfig.getProperty("2", "default"));
//    	System.out.println(AnoleConfig.getProperty("3", "default"));
//    	System.out.println(AnoleConfig.getProperty("4", "default"));
    }
    
    public static void testLocalConfig(){
    	
    	AnoleLoader anoleLoader = new AnoleClasspathLoader();
    	anoleLoader.load();
    	System.out.println(AnoleConfig.getProperty("name"));
    	System.out.println(AnoleConfig.getBoolProperty("autologin"));
    	System.out.println(AnoleConfig.getIntProperty("max_trial_number"));
    	
    }
}
