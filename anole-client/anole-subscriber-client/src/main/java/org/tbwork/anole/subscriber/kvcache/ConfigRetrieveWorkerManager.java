package org.tbwork.anole.subscriber.kvcache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.c_2_s.GetConfigMessage;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.client.StaticConfiguration;  
import org.tbwork.anole.subscriber.exceptions.NotReadyToRetrieveRemoteConfigException;
import org.tbwork.anole.subscriber.exceptions.RetrieveConfigTimeoutException;

@Component
public class ConfigRetrieveWorkerManager {

	static final Logger logger = LoggerFactory.getLogger(ConfigRetrieveWorkerManager.class);
	
	protected static final Map<String, ConfigItem> configMap = new ConcurrentHashMap<String, ConfigItem>();
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(StaticConfiguration.RETRIEVING_THREAD_POOL_SIZE);

	private static AnoleSubscriberClient anoleSubscriberClient = null;

	public void setAnoleSubscriberClient(
			AnoleSubscriberClient anoleSubscriberClient) {
		ConfigRetrieveWorkerManager.anoleSubscriberClient = anoleSubscriberClient;
	}
	
	public static ConfigItem retrieveRemoteConfig(final String key){ 
		final ConfigItem cItem = configMap.get(key);
		try { 
			if(cItem == null) 
				throw new NotReadyToRetrieveRemoteConfigException(key); 
			
			executorService.submit( new Callable<Void>(){ 
				public Void call() throws Exception { 
				  synchronized(cItem)
				  {
					  if(cItem.isLoaded()) //DCL-2
					  {
						  return null;
					  }
					  synchronized(cItem.getKey())
					  {
						  anoleSubscriberClient.sendMessage(new GetConfigMessage(key));
						  cItem.wait(StaticConfiguration.RETRIEVING_CONFIG_TIMEOUT_TIME);  
						  return null;
					  } 
				  }
				} 
			}).get();  
			
			if(!cItem.isLoaded())
				throw new RetrieveConfigTimeoutException(); 
		} catch (RetrieveConfigTimeoutException e) {
			logger.error("[:(] Timeout when anole tried to retrieving config (key = {}) from the remote. Anole will use the default value until the server responses successfully.", key);
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
		ConfigItem cItem = configMap.get(key);
		if(cItem == null) 
			throw new NotReadyToRetrieveRemoteConfigException(key); 
		cItem.setValue(value, type); 
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
