package org.tbwork.anole.subscriber;

import java.util.concurrent.TimeUnit;

import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig;
import org.tbwork.anole.subscriber.core.AnoleLoader;
import org.tbwork.anole.subscriber.core.impl.ClasspathAnoleLoader;

/**
 * Hello world!
 *
 */
public class ClientStart 
{
    public static void main( String[] args )
    {
    	
    	testLocalConfig();
    }
    
    
    public static void testServer(){
    	AnoleLoader anoleLoader = new ClasspathAnoleLoader();
    	anoleLoader.load();
    	AnoleSubscriberClient client = AnoleSubscriberClient.instance();
    	
    	client.connect();
    	try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println(AnoleConfig.getProperty("1", "default"));
    	System.out.println(AnoleConfig.getProperty("2", "default"));
    	System.out.println(AnoleConfig.getProperty("3", "default"));
    	System.out.println(AnoleConfig.getProperty("4", "default"));
    }
    
    public static void testLocalConfig(){
    	
    	AnoleLoader anoleLoader = new ClasspathAnoleLoader();
    	anoleLoader.load();
    	System.out.println(AnoleConfig.getProperty("name"));
    	System.out.println(AnoleConfig.getBoolProperty("autologin"));
    	System.out.println(AnoleConfig.getIntProperty("max_trial_number"));
    	
    }
}
