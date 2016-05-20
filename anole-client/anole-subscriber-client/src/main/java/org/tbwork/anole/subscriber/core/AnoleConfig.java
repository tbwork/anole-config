package org.tbwork.anole.subscriber.core;
 

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap; 

import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.model.ConfigChangeDTO;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.ConfigItem;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient; 
import org.tbwork.anole.subscriber.client.handler.ConfigChangeNotifyMessageHandler;
import org.tbwork.anole.subscriber.core.impl.ChainedConfigObserver; 
import org.tbwork.anole.subscriber.exceptions.AnoleNotReadyException;

import sun.misc.Unsafe;
 
/**
 * <p> Yes! It's an all-in-one tool. You can access
 * any function of Anole here. What does old
 * man always say? Anole in hand, World in pocket! :)
 * @author Tommy.Tang
 */ 
public class AnoleConfig extends AnoleLocalConfig{ 
	  
	private static ObserverManager om =ObserverManager.instance();
	
	/**
	 * <p> Used to register observers who will be notified once
	 * a configuration-change is received, before being processed. 
	 * <p><b>Usage example:</b>
	 * <pre>  
	 *   1. AnoleConfig.registerPreObserver("keyname", new ChainedConfigObserver(false) { 
	 *   2.     @Override
	 *   3.     public void process(ConfigChangeDTO ccDto) {
	 *   4.          ccDto.getKey(); 
	 *   5.          ccDto.getOrigValue();
	 *   6.          ccDto.getDestValue();
	 *   7.          ccDto.getOriConfigType(); 
	 *   8.          ccDto.getDestConfigType(); 
	 *   9.     }
	 *   10.});
	 * </pre>
	 * 
	 * @param key the key will be observed
	 * @param observer the observer
	 */
	public static void registerPreObserver(String key, ChainedConfigObserver observer){
		om.addPreObservers(key, observer);
	}
 
	/**
	 * <p> Used to register observers who will be notified after
	 * a configuration-change is received and processed.
	 * <p><b>Usage example:</b>
	 * <pre>  
	 *   1. AnoleConfig.registerPostObserver("keyname", new ChainedConfigObserver(false) { 
	 *   2.     @Override
	 *   3.     public void process(ConfigChangeDTO ccDto) {
	 *   4.          ccDto.getKey(); 
	 *   5.          ccDto.getOrigValue();
	 *   6.          ccDto.getDestValue();
	 *   7.          ccDto.getOriConfigType(); 
	 *   8.          ccDto.getDestConfigType(); 
	 *   9.     }
	 *   10.});
	 * </pre>
	 * 
	 * @param key the key will be observed
	 * @param observer the observer
	 */
	public static void registerPostObserver(String key, ChainedConfigObserver observer){
		om.addPostObservers(key, observer);
	} 
	
	private static ConfigItem getRemoteConfig(String key)
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
