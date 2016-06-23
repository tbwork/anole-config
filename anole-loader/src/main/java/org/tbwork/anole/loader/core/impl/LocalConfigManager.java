package org.tbwork.anole.loader.core.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import org.tbwork.anole.common.message.c_2_s.subscriber._2_worker.GetConfigMessage;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.ConfigItem;
import org.tbwork.anole.loader.core.ConfigManager;
import org.tbwork.anole.loader.exceptions.CircularDependencyException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.util.StringUtil; 
 
/**
 * LocalConfigManager provides basic operations 
 * for local configurations.
 * @see #setConfigItem(String, String, ConfigType)
 * @see #checkAndInitialConfig(String)
 * @author Tommy.Tang
 */
public class LocalConfigManager implements ConfigManager{

	static final Logger logger = LoggerFactory.getLogger(LocalConfigManager.class);
	
	protected static final Map<String, ConfigItem> configMap = new ConcurrentHashMap<String, ConfigItem>();  
	
	private static final Set<String> unknownConfigSet = new HashSet<String>();
	 
	@Override
	public void setConfigItem(String key, String value, ConfigType type){ 
		if(logger.isDebugEnabled())
			logger.debug("New config found: key = {}, raw value = {}, type = {}", key, value, type);
		if(!AnoleLocalConfig.initialized)//Add to JVM system properties for spring to read.
			System.setProperty(key, value); 
		ConfigItem cItem = configMap.get(key);
		if(cItem == null)  
			cItem = initialConfig(key);
		cItem.setValue(value, type);   
	}
	
	@Override
	public ConfigItem getConfigItem(String key){
		//local first
		ConfigItem result =  configMap.get(key);
		
		//then system
		if(result == null ) {
			String sysProperty = System.getProperty(key);
			if(sysProperty != null)
			{
				result = initialConfig(key);
				result.setValue(sysProperty, ConfigType.STRING);
			}
		} 
		return result;
	}

    
    @Override
    public void postProcess(){
    	initializeContext();
    	recursionBuildConfigMap();
    	cleanEscapeCharacters();
    	if(!unknownConfigSet.isEmpty()){
    		logger.error("There are still some configurations could not be parsed rightly, they are: {} ", unknownConfigSet.toArray().toString() );
    	}
    }
    
	
    public ConfigItem initialConfig(String key){   
    	ConfigItem cItem = new ConfigItem(key);
		configMap.put(key, cItem);  
		return cItem;
	}  
    /**
     * Replace those variable-values with concrete values recursively.
     * E.g., a snippet of Anole configuration is as following:
     * <pre>
     * ip=127.0.0.1
     * port=80
     * connectionString=#{ip}:#{port}
     * </pre>
     * In this case, after calling this method, the connectionString 
     * would be 127.0.0.1:80
     */
    private void recursionBuildConfigMap(){
    	Set<Entry<String,ConfigItem>> entrySet = configMap.entrySet();
    	for(Entry<String,ConfigItem> item : entrySet){
    		rsc(item.getKey()); 
    	}
    }
    
    /**
     * Clear all escape characters in the configuration value.
     */
    private void cleanEscapeCharacters(){
    	Set<Entry<String,ConfigItem>> entrySet = configMap.entrySet();
    	for(Entry<String,ConfigItem> item : entrySet){
    		cleanEscapeCharactersFromConfigItem(item.getValue());
    	}
    }
     
    
    private void cleanEscapeCharactersFromConfigItem(ConfigItem ci){
    	String strValue = ci.strValue();
    	ci.setValue(StringUtil.replaceEscapeChars(strValue), ci.getType());
    }
    
    /**
     * Recursively re-Set Configurations.
     */
    private String rsc(String key){ 
    	if(unknownConfigSet.contains(key)){
    		throw new CircularDependencyException(key);
    	}
    	unknownConfigSet.add(key);
    	ConfigItem ci = extendibleGetConfigItem(key);
    	if(ci == null) {
    		String message = String.format("The config(key=%s is not existed", key);
			throw new ErrorSyntaxException(key, message);
    	} 
		String [] variblesWithCloth = StringUtil.getVariables(ci.strValue(), key);
		for(String str: variblesWithCloth){
			String vkey = StringUtil.getVariable(str);
			if(vkey==null || vkey.isEmpty())
				throw new ErrorSyntaxException(key, str + " must contains a valid variable.");
			String realValue = rsc(vkey);
			if(realValue == null){
				String message = String.format("The config(key=%s) could not be null value because it is a reference (dependency) of config(key=%s)", vkey, key);
				throw new ErrorSyntaxException(key, message);
			} 
			if(!realValue.equals(str)){ 
				ci.setValue(ci.strValue().replace(str, realValue), ci.getType());
			}
			// else: real value is still not found, keep intact and do nothing
		}  
		unknownConfigSet.remove(key);
    	return ci.strValue(); 
    }
    
    /**
     * This open method allow its children classes to
     * re-define the configuration retrieving way, for
     * example retrieving configurations from a remote 
     * server or other configuration repositories.
     * @see {@link #recursionBuildConfigMap()}
     */
    protected ConfigItem extendibleGetConfigItem(String key){
    	return configMap.get(key);
    }

    protected void initializeContext(){
    	//nothing special for local configuration manager.
    }
    
}
