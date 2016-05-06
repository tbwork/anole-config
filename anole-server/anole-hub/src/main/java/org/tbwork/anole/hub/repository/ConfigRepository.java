package org.tbwork.anole.hub.repository;

import org.tbwork.anole.hub.model.ConfigDO;

/**
 * Configuration repository.
 * @author Tommy.Tang
 */
public interface ConfigRepository {

	/**
	 * Retrieve configuration by specified key.
	 */
	public ConfigDO retrieveConfigByKey(String key, String env);
	 
	/**
	 * Update a configuration item.
	 * @param config the configuration item
	 * @param env    the target environment
	 * @param operator the operator's username
	 */
	public void setConfig(ConfigDO config, String env, String operator);
	
	
	/** 
	 * Create a configuration item.
	 * @param config the configuration item
	 * @param operator the operator's username
	 */
	public void addConfig(ConfigDO config, String operator);
}
