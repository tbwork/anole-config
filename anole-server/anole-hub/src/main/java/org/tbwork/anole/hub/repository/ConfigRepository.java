package org.tbwork.anole.hub.repository;

import org.tbwork.anole.hub.model.ConfigDO;

public interface ConfigRepository {

	public ConfigDO retrieveConfigByKey(String key);
	
	public void setConfig(ConfigDO config, String operator);
	
}
