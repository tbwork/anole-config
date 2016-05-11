package org.tbwork.anole.hub.repository;

import org.tbwork.anole.hub.model.ConfigDO;
import org.tbwork.anole.hub.model.ConfigValueDO;

/**
 * Configuration repository.
 * @author Tommy.Tang
 */
public interface ConfigRepository {

	/**
	 * Retrieve configuration by specified key.
	 */
	public ConfigValueDO retrieveConfigValueByKey(String key, String env);
	
	/**Set a value for certain configuration of certain environment.
	 * @param configValueDo the new configuration value data
	 */
	public void setConfigValue(ConfigValueDO configValueDo); 
	/** 
	 * Create a configuration item.
	 * @param config the configuration item
	 */
	public void addConfig(ConfigDO config); 
	
	/**
	 * @param config the new configuration data
	 */
	public void setConfig(ConfigDO config);
	

	
}
