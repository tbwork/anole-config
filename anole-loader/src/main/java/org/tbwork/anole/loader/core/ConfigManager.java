package org.tbwork.anole.loader.core;

import org.tbwork.anole.common.ConfigType;

public interface ConfigManager {

	public ConfigItem getConfigItem(String key);
	
	public void setConfigItem(String key, String value, ConfigType type);
	
}
