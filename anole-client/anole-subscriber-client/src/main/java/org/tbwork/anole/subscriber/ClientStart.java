package org.tbwork.anole.subscriber;

import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.kvcache.AnoleConfig;

/**
 * Hello world!
 *
 */
public class ClientStart 
{
    public static void main( String[] args )
    {
    	AnoleSubscriberClient client = AnoleSubscriberClient.instance();
    	
    	client.connect();
    	
    	System.out.println(AnoleConfig.getProperty("1", "default"));
    	System.out.println(AnoleConfig.getProperty("2", "default"));
    	System.out.println(AnoleConfig.getProperty("3", "default"));
    	System.out.println(AnoleConfig.getProperty("4", "default"));
    }
}
