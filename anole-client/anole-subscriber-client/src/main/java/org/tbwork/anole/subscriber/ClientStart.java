package org.tbwork.anole.subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.subscriber.client._2_boss.IAnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_boss.impl.AnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_worker.IAnoleSubscriberClient;
import org.tbwork.anole.subscriber.client._2_worker.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig; 
import org.tbwork.anole.subscriber.core.impl.AnoleSubscriberClasspathLoader; 


public class ClientStart 
{
	
	public static final Object connectLock = new Object();
	 
	
    public static void main( String[] args )
    { 
    	 
    }
    
    public static void startUp(){
    	 
    	IAnoleSubscriberClient asc = AnoleSubscriberClient.instance(); 
    	asc.connect();
    	
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
