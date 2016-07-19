package org.tbwork.anole.subscriber.core.impl;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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
import org.tbwork.anole.common.message.c_2_s.subscriber._2_worker.GetConfigMessage;
import org.tbwork.anole.loader.core.ConfigItem;
import org.tbwork.anole.loader.core.impl.LocalConfigManager;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.util.StringUtil;
import org.tbwork.anole.subscriber.client._2_worker.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig;
import org.tbwork.anole.subscriber.exceptions.RetrieveConfigTimeoutException;
import org.tbwork.anole.subscriber.util.GlobalConfig;

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
	
	@Override
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
	
	@Override
	public void setConfigItem(String key, String value, ConfigType type){ 
		if(logger.isDebugEnabled())
			logger.debug("Set config: key = {}, value = {}, type = {}", key, value, type);
		ConfigItem cItem = super.getConfigItem(key);
		if(cItem == null) 
			 cItem = new ConfigItem(key, value, type); 
		else
			 cItem.setValue(value, type);  
		if(logger.isDebugEnabled())
			logger.debug("Notified! (key = {})", key);
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
		Preconditions.checkArgument(cItem.getKey()!=null &&  !(cItem.getKey().isEmpty()), "Config key is null! Please check it and try again.");
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
								  logger.debug("GetConfigMessage (key = {}) sent successfully. Enter waiting...", cItem.getKey());
							  synchronized(cItem.getKey())  {
								  while(!cItem.isLoaded() && !cItem.isGiveup())
								     cItem.getKey().wait(); 
							  }
							  if(logger.isDebugEnabled())  
								  logger.debug("Wait() of {} is over!", cItem.getKey());
						  }  
					  }
				  } 
				  return null;
				} 
			});  
			@SuppressWarnings("unused")
			Void innerResult = getConfigResult.get(GlobalConfig.RETRIEVING_CONFIG_TIMEOUT_TIME, TimeUnit.MILLISECONDS); 
			cItem.setGiveup(true);
			cItem.getKey().notify();
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
	
	/**
	 * Overriding this method can help to:
     * Replace those variable-values with concrete values recursively.
     * E.g., a snippet of Anole configuration is as following:
     * <pre>
     * ip=127.0.0.1
     * connectionString=#{ip}:#{port}
     * </pre>
     * And the #{port} is a remote configuration whose value is: 8080.
     * In this case, after calling this method, the connectionString 
     * would be 127.0.0.1:8080
     */
	@Override
    protected ConfigItem extendibleGetConfigItem(String key){
    	return this.getConfigItem(key);
    }
	
	@Override
	protected void initializeContext(){
		anoleSubscriberClient.connect();   // start the anole subscriber client  
		new AnoleConfig(); // call the codes in the static block. 
    }
     
}
