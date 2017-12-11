package org.tbwork.anole.hub.util;

import java.nio.charset.Charset;

import org.tbwork.anole.loader.core.Anole; 

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

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
    
    public static String [] getPorts(String propertyName, String defaults){
    	try{
    		String portsString = Anole.getProperty(propertyName, defaults);
        	String [] ports = portsString.split(","); 
    		ports[1] = ports[1];
    		return ports;
    	}
    	catch(Exception e){
    		throw new RuntimeException ("Wrong format for the configuration '"+propertyName+"'");
    	} 
    }
    
    public static String md5(String inputString){
    	try{
    		Hasher hasher = Hashing.md5().newHasher();
        	hasher.putString(inputString,  Charset.forName("UTF-8")); 
        	return hasher.hash().toString();
    	}
    	catch(Exception e){
    		throw new RuntimeException(e.getMessage());
    	} 
    }
    
  
    
}
