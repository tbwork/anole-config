package org.tbwork.anole.loader.core;

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
 
/**
 * LocalConfigManager provides basic operations 
 * to store configurations.
 * @see #setConfigItem(String, String, ConfigType)
 * @see #checkAndInitialConfig(String)
 * @author Tommy.Tang
 */
public class LocalConfigManager {

	static final Logger logger = LoggerFactory.getLogger(LocalConfigManager.class);
	
	private static final Map<String, ConfigItem> configMap = new ConcurrentHashMap<String, ConfigItem>();  
	
	private static final LocalConfigManager localConfigManager  = new LocalConfigManager();
	
	private LocalConfigManager(){}
	
	public static LocalConfigManager getInstance(){
		return localConfigManager;
	}
	
	public void setConfigItem(String key, String value, ConfigType type){ 
		if(logger.isDebugEnabled())
			logger.debug("Set config: key = {}, value = {}, type = {}", key, value, type);
		ConfigItem cItem = checkAndInitialConfig(key); 
		cItem.setValue(value, type);  
	}
	
	public ConfigItem getConfigITem(String key){
		return configMap.get(key);
	}
	 
    private ConfigItem checkAndInitialConfig(String key){  
		ConfigItem cItem = configMap.get(key);
		if(cItem != null) 
			return cItem;  
		cItem = new ConfigItem(key);
		configMap.put(key, cItem); 
		return cItem;
	}  
	 
}
