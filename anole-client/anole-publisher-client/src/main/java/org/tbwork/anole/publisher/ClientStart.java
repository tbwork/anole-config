package org.tbwork.anole.publisher;

import java.util.concurrent.TimeUnit; 

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
