package org.tbwork.anole.subscriber.core;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.c_2_s.GetConfigMessage;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.client.GlobalConfig;  
import org.tbwork.anole.subscriber.exceptions.ConfigMapNotReadyToRetrieveRemoteConfigException;
import org.tbwork.anole.subscriber.exceptions.RetrieveConfigTimeoutException;
 
/**
 * ConfigManager maintains a local configuration map
 * and provides basic operations of this map. Besides,
 * it also provides a method {@link ConfigManager#retrieveRemoteConfig(String)}
 * to retrieve configuration from the remote server.
 * @author Tommy.Tang
 */
public class ConfigManager {

	static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	
	protected static final Map<String, ConfigItem> configMap = new ConcurrentHashMap<String, ConfigItem>();
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(GlobalConfig.RETRIEVING_THREAD_POOL_SIZE);

	private static AnoleSubscriberClient anoleSubscriberClient = AnoleSubscriberClient.instance();
	
	public static ConfigItem retrieveRemoteConfig(final String key){ 
		final ConfigItem  cItem= configMap.get(key);
		try { 
			if(cItem == null) 
				throw new ConfigMapNotReadyToRetrieveRemoteConfigException(key); 
			
			Future<Void> getConfigResult = executorService.submit( new Callable<Void>(){ 
				public Void call() throws Exception { 
				  synchronized(cItem)
				  {
					  if(cItem.isLoaded()) //DCL-2 
						  return null;
					  anoleSubscriberClient.sendMessage(new GetConfigMessage(key));
					  if(logger.isDebugEnabled())  
						  logger.debug("GetConfigMessage sent successfully. Enter waiting...");
					  synchronized(cItem.getKey())  {
						  while(!cItem.isLoaded())
						      cItem.getKey().wait(); 
					  }
					  if(logger.isDebugEnabled())  
						  logger.debug("Waked up !");
					  return null;
				  }
				} 
			});  
			Void innerResult = getConfigResult.get(GlobalConfig.RETRIEVING_CONFIG_TIMEOUT_TIME, TimeUnit.MILLISECONDS);

		} catch (TimeoutException e) {
			logger.error("[:(] Timeout (tolerent limit is {}) when anole tried to retrieving config (key = {}) from the remote. Anole will use the default value until the server responses successfully.", GlobalConfig.RETRIEVING_CONFIG_TIMEOUT_TIME, key);
			e.printStackTrace(); 
		} catch (InterruptedException e) {
			logger.error("[:(] Retrieving config thread is interrupted.");
			e.printStackTrace();
		} catch (ExecutionException e) {
			logger.error("[:(] Retrieving config task failed to be executed.");
			e.printStackTrace();
		} finally{
			return cItem;
		}
	}
	
	
	
	public static void setConfigItem(String key, String value, ConfigType type){ 
		if(logger.isDebugEnabled())
			logger.debug("Set config: key = {}, value = {}, type = {}", key, value, type);
		ConfigItem cItem = configMap.get(key);
		if(cItem == null) 
			throw new ConfigMapNotReadyToRetrieveRemoteConfigException(key); 
		cItem.setValue(value, type); 
		if(logger.isDebugEnabled())
			logger.debug("Notified!");
	}
	
	
    public static ConfigItem checkAndInitialConfig(String key){
		
		if(configMap.containsKey(key))
		{
			ConfigItem cItem = configMap.get(key);
			if(cItem != null){
				return cItem;
			}
		} 
		ConfigItem cItem = new ConfigItem(key);
		configMap.put(key, cItem); 
		return cItem;
	}  
	 
}
