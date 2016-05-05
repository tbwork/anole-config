package org.tbwork.anole.hub.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tbwork.anole.hub.localcache.LocalCache;
import org.tbwork.anole.hub.model.ConfigDO;
import org.tbwork.anole.hub.repository.ConfigRepository; 

public class ConfigRepositoryImpl implements ConfigRepository{

	@Autowired
	private LocalCache localCache;
	
	@Override
	public ConfigDO retrieveConfigByKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfig(ConfigDO config, String operator) {
		// TODO Auto-generated method stub
		
	}
 
}
