package org.tbwork.anole.loader.core.manager;

import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.types.ConfigType;

public interface ConfigManager {
	
	/**
	 * Get the configuration item by its key.
	 * @param key the key of the configuration item.
	 * @return the configuration item.
	 */
	public ConfigItem getConfigItem(String key);
	
	/**
	 * Set configuration item into the Anole.
	 * @param key the key of the configuration item.
	 * @param value the value of the configuration item.
	 * @param type the type of the configuration item.
	 */
	public void setConfigItem(String key, String value, ConfigType type);
	
	/**
	 * Tasks need to be executes after loading configurations from the file.
	 */
	public void postProcess();
}
