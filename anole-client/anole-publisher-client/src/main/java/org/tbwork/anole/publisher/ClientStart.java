package org.tbwork.anole.publisher;

import java.util.concurrent.TimeUnit;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.publisher.client.IAnolePublisherClient;
import org.tbwork.anole.publisher.client.impl.AnolePublisherClient; 
/**
 * Hello world!
 *
 */
public class ClientStart 
{
    public static void main( String[] args )
    { 
    	AnoleLoader anoleLoader = new AnoleClasspathLoader();
    	anoleLoader.load();  
    	IAnolePublisherClient apc = AnolePublisherClient.instance();
  	    apc.connect();
    }
     
}
