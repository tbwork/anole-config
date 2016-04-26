package org.tbwork.anole.subscriber.kvcache;
 

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
 
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient; 

import sun.misc.Unsafe;
 
/**
 * Provides different configuration retrieving methods. 
 * @author Tommy.Tang
 */ 
public class AnoleConfig { 
	/**
	 * @param key 
	 * @param defaultValue
	 * @return
	 */
	public static String getProperty(String key, String defaultValue){ 
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty()? defaultValue : cItem.strValue();
	}
	
	public static <T> T getObject(String key, Class<T> clazz){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? null : cItem.objectValue(clazz); 
	}
	
	public static int getIntProperty(String key, int defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.intValue();  
	}
	
	public static short getShortProperty(String key, short defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.shortValue();  
	}
	
	public static long getLongProperty(String key, long defaultValue){
		 ConfigItem cItem = getConfig(key);
		 return cItem.isEmpty() ? defaultValue : cItem.longValue();
	}
	
	public static double getDoubleProperty(String key, double defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.doubleValue();  
	}
	
	
	public static float getFloatProperty(String key, float defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.floatValue();  
	}
	
	
	public static boolean getBoolProperty(String key, boolean defaultValue){
		 ConfigItem cItem = getConfig(key);
	 	 return cItem.isEmpty() ? defaultValue : cItem.boolValue();
	}
	
	private static ConfigItem getConfig(String key)
	{
		 ConfigItem cItem = ConfigRetrieveWorkerManager.checkAndInitialConfig(key); 
		 if(!cItem.isLoaded())
		 {  
			 // retrieve from the remote server
			 cItem = ConfigRetrieveWorkerManager.retrieveRemoteConfig(key);  
		 }
		 return cItem;
	}
	
	
	 
}
