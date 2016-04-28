package org.tbwork.anole.subscriber.core;
 

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
 

import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient; 
import org.tbwork.anole.subscriber.exceptions.AnoleNotReadyException;

import sun.misc.Unsafe;
 
/**
 * Provides different configuration retrieving methods. 
 * @author Tommy.Tang
 */ 
public class AnoleConfig { 
	
	/**
	 * Indicates that local anole is loaded successfully.
	 */
	public static boolean initialized = false;
	
	public static String getProperty(String key, String defaultValue){ 
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty()? defaultValue : cItem.strValue();
	}
	
	public static String getProperty(String key){ 
		 return getProperty(key, null);
	}
	
	public static <T> T getObject(String key, Class<T> clazz){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? null : cItem.objectValue(clazz); 
	}
	
	public static int getIntProperty(String key, int defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.intValue();  
	}
	
	public static int getIntProperty(String key){
		 return getIntProperty(key, 0);  
	}
	
	public static short getShortProperty(String key, short defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.shortValue();  
	}
	
	public static short getShortProperty(String key){
		 return getShortProperty(key, (short)0);
	}
	
	public static long getLongProperty(String key, long defaultValue){
		 ConfigItem cItem = getConfig(key);
		 return cItem.isEmpty() ? defaultValue : cItem.longValue();
	}
	
	public static long getLongProperty(String key){
		 return getLongProperty(key, 0);
	}
	
	public static double getDoubleProperty(String key, double defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.doubleValue();  
	}
	
	public static double getDoubleProperty(String key){
		 return getDoubleProperty(key, 0);
	}
	
	
	public static float getFloatProperty(String key, float defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.floatValue();  
	}
	
	public static float getFloatProperty(String key){
		 return getFloatProperty(key,0f);
	}
	
	
	public static boolean getBoolProperty(String key, boolean defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.boolValue();
	}
	
	public static boolean getBoolProperty(String key){
		 return getBoolProperty(key, false);
	}
	
	private static ConfigItem getConfig(String key)
	{ 
		 if(!initialized)
			 throw new AnoleNotReadyException();
		 ConfigItem cItem = ConfigManager.checkAndInitialConfig(key); 
		 if(!cItem.isLoaded())
		 {  
			 // retrieve from the remote server
			 cItem = ConfigManager.retrieveRemoteConfig(key);  
		 }
		 return cItem;
	} 
}
