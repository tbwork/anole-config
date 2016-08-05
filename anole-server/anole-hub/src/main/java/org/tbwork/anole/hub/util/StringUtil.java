package org.tbwork.anole.hub.util;

import org.tbwork.anole.loader.core.AnoleLocalConfig;

public class StringUtil { 
	
    public static int getPort(String propertyName, String defaults){
    	String [] ports = null;
    	int port = 0;
    	try{ 
    		ports =  getPorts(propertyName, defaults);
        	port = SystemUtil.isPortUsing(Integer.valueOf(ports[0])) ? Integer.valueOf(ports[1]) : Integer.valueOf(ports[0]);  
    	}
    	catch(Exception e){
    		throw new RuntimeException ("Wrong format for the configuration '"+propertyName+"'");
    	}  	
    	if(SystemUtil.isPortUsing(port))
    		throw new RuntimeException("The ports " + ports[0] + ", " + ports[1] + " are already occupied." );
    	return port;
    }
    
    private static String [] getPorts(String propertyName, String defaults){
    	try{
    		String portsString = AnoleLocalConfig.getProperty(propertyName, defaults);
        	String [] ports = portsString.split(","); 
    		ports[1] = ports[1];
    		return ports;
    	}
    	catch(Exception e){
    		throw new RuntimeException ("Wrong format for the configuration '"+propertyName+"'");
    	} 
    }
}
