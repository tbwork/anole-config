package org.tbwork.anole.loader.core.manager;

import org.tbwork.anole.loader.core.manager.source.RemoteRetriever;
import org.tbwork.anole.loader.core.manager.source.SourceRetriever;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.core.model.RawKV;

import java.util.List;

public interface ConfigManager {


	/**
	 * Register raw key-values to the manager.
	 * @param rawKVList
	 */
	public void batchRegisterDefinition(List<RawKV> rawKVList);

	/**
	 * Set configuration item into the Anole. Deeply digging will be used if
	 * value contains an other property like: a = ${b}-service
	 * @param key the key of the configuration item.
	 * @param definition the definition of the configuration item.
	 * @return the registered config item.
	 */
	public ConfigItem registerAndSetValue(String key, String definition);

	/**
	 * Lookup the key's value from all sources, and then register it.
	 * If no value is found, just initialize an empty configItem.
	 * @param key the given key.
	 * @return the registered config item.
	 */
	public ConfigItem registerFromAnywhere(String key);

	/**
	 * Set configuration item into the Anole. Deeply digging will be used if
	 * value contains an other property like: a = ${b}-service
	 * @param key the key of the configuration item.
	 * @param definition the definition of the configuration item.
	 * @param updateTime the update time given by the caller
	 * @return the registered config item.
	 */
	public ConfigItem registerAndSetValue(String key, String definition, long updateTime);


	/**
	 * Register all configs with concrete value (strValue is not null), to the system.
	 */
	public void registerToSystem();


	/**
	 * Remove all configs registered by {@link #registerToSystem()} from system property manager.
	 */
	public void removeFromSystem();


	/**
	 * Judge whether the given key is interested by the current application.
	 * @param key the key of the configuration item.
	 * @return true if the application interests the given key.
	 */
	public boolean interest(String key);

	/**
	 * Get the configuration item by its key.
	 * @param key the key of the configuration item.
	 * @return the configuration item.
	 */
	public ConfigItem getConfigItem(String key);


	/**
	 * Rebuild all configurations and refresh their values.
	 * @param needCheckIntegrity true means all config should be concrete after the refresh, otherwise log as an error; false means do not check
	 */
	public void refresh(boolean needCheckIntegrity);

	/**
	 * Add an extended retriever to retrieve configuration.
	 * @param sourceRetriever the extended retriever
	 */
	void addExtensionRetriever(SourceRetriever sourceRetriever);



	/**
	 * Start an update recorder to receive all config update events from the
	 * remote servers like apollo, spring config etc..
	 */
	void startUpdateRecorder();


	/**
	 * Start an executor to process all update events stored in the update recorder.
	 */
	void startUpdateExecutor();


	/**
	 * Tell the update manager to shut down.
	 */
	void stopUpdateManager();

	/**
	 * Apply a update to a config. This operation means the change request will
	 * be put into the update request queue, waiting for further process.
	 * @param key
	 * @param newValue
	 */
	void applyChange(String key, String newValue);


}
