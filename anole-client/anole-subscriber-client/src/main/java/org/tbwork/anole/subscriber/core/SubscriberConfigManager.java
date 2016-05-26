package org.tbwork.anole.subscriber.core;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.KeyGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.c_2_s.GetConfigMessage;
import org.tbwork.anole.loader.core.ConfigItem;
import org.tbwork.anole.loader.core.impl.LocalConfigManager;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.client.GlobalConfig;    
import org.tbwork.anole.subscriber.exceptions.RetrieveConfigTimeoutException;

import com.google.common.base.Preconditions;
 
/**
 * This class provides management for remote configuration
 * repository.
 * @author Tommy.Tang
 */
public class SubscriberConfigManager extends LocalConfigManager{

	static final Logger logger = LoggerFactory.getLogger(SubscriberConfigManager.class);
	
	private static final SubscriberConfigManager cm = new SubscriberConfigManager();
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(GlobalConfig.RETRIEVING_THREAD_POOL_SIZE);

	private static AnoleSubscriberClient anoleSubscriberClient = AnoleSubscriberClient.instance(); 
	
	private final Object createSkeletonLock = new Object();
	
	private SubscriberConfigManager(){}
	
	public static SubscriberConfigManager getInstance(){
		return cm;
	}
	
	public ConfigItem getConfigItem(String key){
		// check local configuration repository
		ConfigItem ci = super.getConfigItem(key);
		if(ci != null && ci.isLoaded()) return ci;
		
		// initialize skeleton
		if(ci == null)
			ci = initializeSkeleton(key);
		
		// retrieve from the remote repository
	    return retrieveRemoteConfig(ci); 
	}
	
	
	public void setConfigItem(String key, String value, ConfigType type){ 
		if(logger.isDebugEnabled())
			logger.debug("Set config: key = {}, value = {}, type = {}", key, value, type);
		ConfigItem cItem = super.getConfigItem(key);
		if(cItem == null) 
			 cItem = new ConfigItem(key, value, type); 
		else
			 cItem.setValue(value, type); 
		if(logger.isDebugEnabled())
			logger.debug("Notified!");
	}
	
	private ConfigItem initializeSkeleton(String key){
		ConfigItem ci = super.getConfigItem(key);
		if(ci == null){
			synchronized(createSkeletonLock){
				ci = super.getConfigItem(key);
				if(ci == null){
					return super.initialConfig(key);
				}
			} 
		} 
		return ci;
	}
	
	private ConfigItem retrieveRemoteConfig(final ConfigItem cItem){ 
		Preconditions.checkNotNull(cItem, "Config item is null! Please check it and try again.");
		try {      
			Future<Void> getConfigResult = executorService.submit( new Callable<Void>(){ 
				public Void call() throws Exception { 
				  if(!cItem.isLoaded()){
					  synchronized(cItem)
					  { 
						  if(!cItem.isLoaded())
						  {
							  anoleSubscriberClient.sendMessage( new GetConfigMessage(cItem.getKey(), AnoleConfig.getProperty("env")));
							  if(logger.isDebugEnabled())  
								  logger.debug("GetConfigMessage sent successfully. Enter waiting...");
							  synchronized(cItem.getKey())  {
								  while(!cItem.isLoaded())
								      cItem.getKey().wait(); 
							  }
							  if(logger.isDebugEnabled())  
								  logger.debug("Waked up !");
						  }  
					  }
				  } 
				  return null;
				} 
			});  
			Void innerResult = getConfigResult.get(GlobalConfig.RETRIEVING_CONFIG_TIMEOUT_TIME, TimeUnit.MILLISECONDS); 
		} catch (TimeoutException e) {
			logger.error("[:(] Timeout (tolerent limit is {}) when anole tried to retrieving config (key = {}) from the remote. Anole will use the default value until the server responses successfully.", GlobalConfig.RETRIEVING_CONFIG_TIMEOUT_TIME, cItem.getKey());
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

}
