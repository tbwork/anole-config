package org.tbwork.anole.subscriber;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader; 
import org.tbwork.anole.subscriber.client._2_boss.IAnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_boss.impl.AnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_worker.IAnoleSubscriberClient;
import org.tbwork.anole.subscriber.client._2_worker.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig; 
import org.tbwork.anole.subscriber.core.impl.AnoleSubscriberClasspathLoader;

import com.alibaba.fastjson.JSON; 


public class ClientStart 
{
	
	public static final Object connectLock = new Object();
	 
	
    public static void main( String[] args ) throws UnknownHostException
    { 
    	testAnole();
    }
    
    public static void startUp(){
    	AnoleLoader anoleLoader = new AnoleSubscriberClasspathLoader();
    	anoleLoader.load();   
    }
    
    public static void testAnole(){
    	 
    	AnoleLoader anoleLoader = new AnoleSubscriberClasspathLoader();
    	anoleLoader.load();  
    	Scanner scan = new Scanner(System.in);
  	    while(true){
  	    	int a = scan.nextInt();
  	    	if(a == 0)
  	    		break;
  	    	System.out.println("Get config... "); 
  	    	System.out.println(AnoleConfig.getProperty("key1", "default")); 
  	  	    
  	    }
    	
    
    }
    
    public static void testLocalConfig(){
    	
    	AnoleLoader anoleLoader = new AnoleClasspathLoader();
    	anoleLoader.load();
    	System.out.println(AnoleConfig.getProperty("cs")); 
    }
}
