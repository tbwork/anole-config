package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.core.model.ConfigItem;
import com.github.tbwork.anole.loader.exceptions.AnoleNotReadyException;
import com.github.tbwork.anole.loader.exceptions.ConfigNotSetException;
import com.github.tbwork.anole.loader.exceptions.EnvironmentNotSetException;
import com.github.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import com.github.tbwork.anole.loader.statics.BuiltInConfigKeys;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.S;
import com.github.tbwork.anole.loader.core.manager.ConfigManager;
import com.github.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import com.github.tbwork.anole.loader.core.manager.AnoleValueManager;

/**
 * <p> Anole provides basic retrieving 
 * operations on local configurations. 
 * @author Tommy.Tang
 */ 
public class Anole { 
	 
	protected static final ConfigManager cm = AnoleConfigManager.getInstance();

	private final static AnoleLogger logger = new AnoleLogger(Anole.class);

	/**
	 * Indicates that local anole is loaded successfully.
	 */
	public static volatile boolean initialized = false;

	public static void refreshContext(boolean needCheckIntegrity){
		cm.refresh(needCheckIntegrity);
	}

	/**
	 * Check whether the value of the key is existing and
	 * not blank, or not.
	 * @param key the target key.
	 * @return <b>true</b> if the corresponding value of the key is not existing 
	 * or blank, otherwise return <b>false</b>.
	 */
	public static boolean isPropertyEmptyOrNotExist(String key) {
		String value = getProperty(key);
		return S.isEmpty(value);
	}
	
	public static String getProperty(String key, String defaultValue){
		ConfigItem cItem = getConfig(key, cm);
		return  cItem == null || S.isEmpty(cItem.strValue()) ? defaultValue : cItem.strValue();
	}
	
	public static String getProperty(String key){ 
		 return getProperty(key, null);
	}

	public static int getIntProperty(String key, int defaultValue){
		ConfigItem cItem = getConfig(key, cm);
		return  cItem == null || S.isEmpty(cItem.strValue()) ? defaultValue : cItem.intValue();
	}
	
	public static int getIntProperty(String key){
		 return getIntProperty(key, 0);  
	}
	
	public static short getShortProperty(String key, short defaultValue){
		ConfigItem cItem = getConfig(key, cm);
		return   cItem==null || cItem.strValue() == null ? defaultValue : cItem.shortValue();
	}
	
	public static short getShortProperty(String key){
		 return getShortProperty(key, (short)0);
	}
	
	public static long getLongProperty(String key, long defaultValue){
		ConfigItem cItem = getConfig(key, cm);
		return   cItem==null || cItem.strValue() == null ? defaultValue : cItem.longValue();
	}
	
	public static long getLongProperty(String key){
		 return getLongProperty(key, 0);
	}
	
	public static double getDoubleProperty(String key, double defaultValue){
		ConfigItem cItem = getConfig(key, cm);
		return   cItem==null || cItem.strValue() == null ? defaultValue : cItem.doubleValue();
	}
	
	public static double getDoubleProperty(String key){
		 return getDoubleProperty(key, 0);
	}
	
	
	public static float getFloatProperty(String key, float defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.strValue() == null ? defaultValue : cItem.floatValue();
	}
	
	public static float getFloatProperty(String key){
		 return getFloatProperty(key,0f);
	}
	
	
	public static boolean getBoolProperty(String key, boolean defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return  cItem==null || cItem.strValue() == null ? defaultValue : cItem.boolValue();
	}
	
	public static boolean getBoolProperty(String key){
		 return getBoolProperty(key, false);
	}
	
	public static void setProperty(String key, String value){
		if(AnoleValueManager.containVariable(value) || AnoleValueManager.isExpression(value)){
			throw new OperationNotSupportedException("Before initialization, you can and only can set a plain value for the given key.");
		}
		cm.registerAndSetValue(key, value);
	}

	/**
	 * Set the key value to Anole-config, and then set to system property.
	 *
	 * @param key the given key
	 * @param value the given value
	 */
	public static void setSysProperty(String key, String value){
		setProperty(key, value);
		System.setProperty(key,value);
	}

	/**
	 * Whether the specified key is defined or not.
	 * @param key the specified key
	 * @return return if the key is already defined.
	 */
	public static boolean isPresent(String key){
		return cm.getConfigItem(key) != null;
	}

	/**
	 * Get the rawValue of specified key. The raw value is the original value defined
	 * by users which may contain another key.
	 * E.g.,
	 * <pre>
	 *     a = b ;
	 *     b = ${a}.123
	 * </pre>
	 * getRawValue("b") will return "${a}.123"
	 * @param key
	 * @return
	 */
	public static String getRawValue(String key){
		ConfigItem configItem = cm.getConfigItem(key);
		if(configItem != null){
			if(configItem.strValue() != null){
				return configItem.strValue();
			}
			return configItem.getDefinition();
		}
		return null;
	}

	public static String getEnvironment(){
		ConfigItem cItem = cm.getConfigItem("anole.env");
		if(cItem == null){
			throw new EnvironmentNotSetException();
		}
		return cItem.strValue();
	}



	protected static ConfigItem getConfig(String key, ConfigManager cm)
	{ 
		 if(!initialized && !BuiltInConfigKeys.isBuiltInKeys(key)){
			 logger.error("Anole is not initialized yet. The key ({}) is not available now!", key);
			 throw new AnoleNotReadyException(key);
		 }

		 int index = key.indexOf(":");
		 ConfigItem cItem = cm.getConfigItem(key);

		 if(cItem == null){
		 	// Attempt to load the config from extension source
			 cItem = cm.registerFromEverywhere(key);
		 }

		 if(cItem == null){
		 	logger.debug("An attempt to get key ‘{}’ failed due to that it does not exist.", key);
		 	return null;
		 }

		 if(S.isNotEmpty(cItem.getError())){
			throw new ConfigNotSetException(cItem.getKey(), "There is a related config which is not set value yet, details message: " + cItem.getError());
		 }

		 return cItem;
	} 

}
