package org.tbwork.anole.loader.core.impl;

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
import org.tbwork.anole.common.message.c_2_s.GetConfigMessage;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.ConfigItem;
import org.tbwork.anole.loader.core.ConfigManager;
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
	
	private static final Map<String, ConfigItem> configMap = new ConcurrentHashMap<String, ConfigItem>();  
	
	@Override
	public void setConfigItem(String key, String value, ConfigType type){ 
		if(logger.isDebugEnabled())
			logger.debug("Set config: key = {}, value = {}, type = {}", key, value, type);
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
     * 
     */
    protected void recursionBuildConfigMap(){
    	Set<Entry<String,ConfigItem>> entrySet = configMap.entrySet();
    	for(Entry<String,ConfigItem> item : entrySet){
    		rsc(item.getKey()); 
    	}
    }
     
    /**
     * Recursively Set Configurations.
     */
    private String rsc(String key){
    	
    	ConfigItem ci = configMap.get(key);
    	if(ci == null) {
    		String message = String.format("The config(key=%s) cound is not existed", key);
			throw new ErrorSyntaxException(key, message);
    	} 
		String [] variblesWithCloth = StringUtil.getVariables(ci.strValue(), key);
		for(String str: variblesWithCloth){
			String vkey = StringUtil.getVariable(str);
			if(vkey==null || vkey.isEmpty())
				throw new ErrorSyntaxException(key, str + " must contains a valid variable.");
			String realValue = rsc(vkey);
			if(realValue == null){
				String message = String.format("The config(key=%s) cound not be null value because it is a reference (dependency) of config(key=%s)", vkey, key);
				throw new ErrorSyntaxException(key, message);
			} 
			if(!realValue.equals(str)){ 
				ci.setValue(ci.strValue().replace(str, realValue), ci.getType());
			}
			// else: real value is still not found, keep intact and do nothing
		}  
    	return ci.strValue(); 
    }
    
    public static void main(String[] args) {
    	LocalConfigManager lcm = new LocalConfigManager();
    	lcm.setConfigItem("a", "1", ConfigType.STRING);
    	lcm.setConfigItem("b", "#{a}", ConfigType.NUMBER);
    	lcm.setConfigItem("c", "#{b}:#{a}", ConfigType.STRING);
    	lcm.recursionBuildConfigMap();
    	System.out.println(lcm.getConfigItem("c").strValue());
	}
	 
}
