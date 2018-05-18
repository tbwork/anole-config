package org.tbwork.anole.loader.context;
  
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.exceptions.AnoleNotReadyException;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.SingletonFactory;
import org.tbwork.anole.loader.util.StringUtil;
 
/**
 * <p> Anole provides basic retrieving 
 * operations on local configurations. 
 * @author Tommy.Tang
 */ 
public class Anole { 
	 
	protected static volatile ConfigManager cm = SingletonFactory.getLocalConfigManager();
	
	/**
	 * Indicates that local anole is loaded successfully.
	 */
	public static boolean initialized = false;
	
	private static boolean runingInJar;
	
	private static Class<?> rootMainClass;
	
	private static Class<?> userMainClass;
	
	private static String environment;
	
	public static void setRootMainClass(Class<?> clazz) {
		rootMainClass = clazz;
	}
	
	public static void setEnvironment(String env) {
		environment = env;
	}
	
	public static void setUserMainClass(Class<?> clazz) {
		userMainClass = clazz;
	}
	
	public static String getEnvironment() {
		return environment;
	} 
	
	/**
	 * The root main class in Anole refers to the main class 
	 * of current java application.
	 */
	public static Class<?> getRootMainClass(){ 
		return rootMainClass; 
	}
	
	/**
	 * The user main class in Anole refers to the class which contains 
	 * a main method calling the Anole boot class ({@link Anole},{@link AnoleConfigContext}, etc.)
	 * directly.
	 */
	public static Class<?> getUserMainClass(){ 
		return userMainClass; 
	}
	
	public static String getCurrentEnvironment(){
		return getProperty("anole.runtime.currentEnvironment");
	}
	
	
	/**
	 * <p> For <b>maven</b> projects, this method will return the artifactId.
	 * <p> For <b>other</b> projects, this method will return the value of 
	 * variable named "anole.project.info.name", you should define it first in your
	 * configuration files.
	 * @return the project name
	 */
	public static String getProjectName() {
		String projectName = Anole.getProperty("artifactId");
		if(projectName == null)
			projectName = Anole.getProperty("anole.project.info.name");
		return projectName;
	}
	 
	
	/**
	 * <p> For <b>maven</b> projects, this method will return the version.
	 * <p> For <b>other</b> projects, this method will return the value of 
	 * variable named "anole.project.info.version", you should define it first in your
	 * configuration files.
	 * @return the project version
	 */
	public static String getProjectVersion() {
		String projectVersion = Anole.getProperty("version");
		if(projectVersion == null)
			projectVersion = Anole.getProperty("anole.project.info.version");
		return projectVersion;
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
		return StringUtil.isNullOrEmpty(value);
	}
	
	public static String getProperty(String key, String defaultValue){ 
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.isEmpty()? defaultValue : cItem.strValue();
	}
	
	public static String getProperty(String key){ 
		 return getProperty(key, null);
	}
	
	public static <T> T getObject(String key, Class<T> clazz){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.isEmpty() ? null : cItem.objectValue(clazz); 
	}
	
	public static int getIntProperty(String key, int defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.isEmpty() ? defaultValue : cItem.intValue();  
	}
	
	public static int getIntProperty(String key){
		 return getIntProperty(key, 0);  
	}
	
	public static short getShortProperty(String key, short defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.isEmpty() ? defaultValue : cItem.shortValue();  
	}
	
	public static short getShortProperty(String key){
		 return getShortProperty(key, (short)0);
	}
	
	public static long getLongProperty(String key, long defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
		 return cItem==null || cItem.isEmpty() ? defaultValue : cItem.longValue();
	}
	
	public static long getLongProperty(String key){
		 return getLongProperty(key, 0);
	}
	
	public static double getDoubleProperty(String key, double defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.isEmpty() ? defaultValue : cItem.doubleValue();  
	}
	
	public static double getDoubleProperty(String key){
		 return getDoubleProperty(key, 0);
	}
	
	
	public static float getFloatProperty(String key, float defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.isEmpty() ? defaultValue : cItem.floatValue();  
	}
	
	public static float getFloatProperty(String key){
		 return getFloatProperty(key,0f);
	}
	
	
	public static boolean getBoolProperty(String key, boolean defaultValue){
		 ConfigItem cItem = getConfig(key, cm);
	 	 return cItem==null || cItem.isEmpty() ? defaultValue : cItem.boolValue();
	}
	
	public static boolean getBoolProperty(String key){
		 return getBoolProperty(key, false);
	}
	
	public static boolean runingInJar(){
		return runingInJar;
	}
	
	public static void setRuningInJar(boolean runingInJar){
		Anole.runingInJar = runingInJar;
	}
	
	protected static ConfigItem getConfig(String key, ConfigManager cm)
	{ 
		 if(!initialized)
			 throw new AnoleNotReadyException();  
		 return cm.getConfigItem(key);
	} 
}
