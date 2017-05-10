package org.tbwork.anole.hub.repository;

import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.hub.model.ConfigDO;
import org.tbwork.anole.hub.model.ConfigValueDO;

/**
 * Configuration repository used in local memory.
 * @author Tommy.Tang
 */
public interface ConfigRepository {

	/**
	 * Retrieve configuration by specified key.
	 */
	public ConfigValueDO retrieveConfigValueByKey(String key, String env);
	
	/**
	 * Set a value for certain configuration of certain environment. 
	 */
	public void setConfigValue(String key, String value, String env, ConfigType configType);  
	
}
