package org.tbwork.anole.loader.core.manager.impl;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.exceptions.CircularDependencyException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.StringUtil;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * LocalConfigManager provides basic operations 
 * for local configurations.
 * @see #setConfigItem(String, String, ConfigType)
 * @see #getConfigItem(String)
 * @author Tommy.Tang
 */
public class LocalConfigManager implements ConfigManager{

	private AnoleLogger logger;
	
	protected static final Map<String, ConfigItem> configMap = new ConcurrentHashMap<String, ConfigItem>();  
	
	private static final Set<String> unknownConfigSet = new HashSet<String>();
	
	private String currentRunEnvironment;
	
	@Override
	public void setConfigItem(String key, String value, ConfigType type){  
		if(!Anole.initialized)//Add to JVM system properties for other frameworks to read.
			System.setProperty(key, value); 
		ConfigItem cItem = configMap.get(key);
		String operation = "New";
		if(cItem == null)  
			cItem = initialConfig(key);
		else
			 operation = "Update";
		if(AnoleLogger.anoleLogLevel != null && logger.isDebugEnabled())
			logger.debug("{} config found: key = {}, raw value = {}, type = {}", operation, key, value, type);
		cItem.setValue(value, type);
		
	}
	
	@Override
	public ConfigItem getConfigItem(String key){
		//local first
		ConfigItem result =  configMap.get(key);
		
		//then system
		if(result == null ) {
			String sysProperty = System.getProperty(key);
			if(sysProperty != null)
			{
				result = initialConfig(key);
				result.setValue(sysProperty, ConfigType.STRING);
			}
		} 
		return result;
	}

    
    @Override
    public void postProcess(){
    	initializeContext();
    	recursionBuildConfigMap();
    	cleanEscapeCharacters();
    	calculateExpression();
    	registerProjectInfo();
    	if(!unknownConfigSet.isEmpty()){
    		logger.error("There are still some configurations could not be parsed rightly, they are: {} ", unknownConfigSet.toArray().toString() );
    	}
    }
    
	
    public ConfigItem initialConfig(String key){   
    	ConfigItem cItem = new ConfigItem(key);
		configMap.put(key, cItem);  
		return cItem;
	}  
    /**
     * Replace those variable-values with concrete values recursively.
     * E.g., a snippet of Anole configuration is as following:
     * <pre>
     * ip=127.0.0.1
     * port=80
     * connectionString=#{ip}:#{port}
     * </pre>
     * In this case, after calling this method, the connectionString 
     * would be 127.0.0.1:80
     */
    private void recursionBuildConfigMap(){
    	Set<Entry<String,ConfigItem>> entrySet = configMap.entrySet();
    	for(Entry<String,ConfigItem> item : entrySet){
    		rsc(item.getKey());
    		if(!Anole.initialized)//Add to JVM system properties for other frameworks to read.
				System.setProperty(item.getKey(), item.getValue().strValue()); 
    	}
    }


    private String parseThreeElementExpression(String key, String value){

    	String [] firstElements = value.split(" \\? ");
    	if(firstElements.length == 2){
			String firstElement = firstElements[0].trim();
			String [] secondELements = firstElements[1].split(" : ");
			if(secondELements.length == 2){
				String secondElement = secondELements[0];
				String thirdElement = secondELements[1];
				if("true".equals(firstElement)){
					return secondElement;
				}
				else if("false".equals(firstElement)) {
					return thirdElement;
				}
			}
		}

		String message = String.format("The right three-element-expression should be '${boolean_value} ? ${true_result} : ${false_result}' while yours is '%s'", value);
		throw new ErrorSyntaxException(key, message);

	}

	/**
	 * Anole support the three element expression like `boolean ? a : b`
	 */
    private void calculateExpression(){
		Set<Entry<String,ConfigItem>> entrySet = configMap.entrySet();
		for(Entry<String,ConfigItem> item : entrySet){
			String value = item.getValue().strValue().trim();
			if(value != null && ( value.startsWith("@@@") || value.startsWith("```"))){
				item.getValue().setValue(parseThreeElementExpression(item.getKey(), value.substring(3)), item.getValue().getType());
				if(!Anole.initialized)//Add to JVM system properties for other frameworks to read.
					System.setProperty(item.getKey(), item.getValue().strValue());
			}
		}
	}


    /**
     * Clear all escape characters in the configuration value.
     */
    private void cleanEscapeCharacters(){
    	Set<Entry<String,ConfigItem>> entrySet = configMap.entrySet();
    	for(Entry<String,ConfigItem> item : entrySet){
    		cleanEscapeCharactersFromConfigItem(item.getValue());
    	}
    }
     
    
    private void cleanEscapeCharactersFromConfigItem(ConfigItem ci){
    	String strValue = ci.strValue();
    	ci.setValue(StringUtil.replaceEscapeChars(strValue), ci.getType());
    }
    
    /**
     * Recursively re-Set Configurations.
     */
    private String rsc(String key){ 
    	if(unknownConfigSet.contains(key)){
    		throw new CircularDependencyException(key);
    	}
    	unknownConfigSet.add(key);
    	ConfigItem ci = extendibleGetConfigItem(key);
    	if(ci == null || ci.strValue() == null) {
    		String message = String.format("There is no manual-set or default-set value for %s", key);
			throw new ErrorSyntaxException(key, message);
    	} 
		String [] variblesWithCloth = StringUtil.getVariables(ci.strValue(), key);
		for(String str: variblesWithCloth){
			String vkey = StringUtil.getVariable(str);
			if(vkey==null || vkey.isEmpty())
				throw new ErrorSyntaxException(key, str + " must contains a valid variable.");
			String realValue = rsc(vkey);
			if(realValue == null){
				String message = String.format("The config(key=%s) could not be null value because it is a reference (dependency) of config(key=%s)", vkey, key);
				throw new ErrorSyntaxException(key, message);
			} 
			if(!realValue.equals(str)){ 
				ci.setValue(ci.strValue().replace(str, realValue), ci.getType()); 
			}
			// else: real value is still not found, keep intact and do nothing
		}  
		unknownConfigSet.remove(key);
    	return ci.strValue(); 
    }
    
    /**
     * This open method allow its children classes to
     * re-define the configuration retrieving way, for
     * example retrieving configurations from a remote 
     * server or other configuration repositories.
     * @param key the key 
     */
    protected ConfigItem extendibleGetConfigItem(String key){
    	return configMap.get(key);
    }

    protected void initializeContext(){
    	//nothing special for local configuration manager.
    }
    
    private void registerProjectInfo() {
    	// get current classpath which the application running in.
    	URL url = Thread.currentThread().getContextClassLoader().getResource("/");
    	if(url != null) {
    		String classpathString = url.toString();
    		if(classpathString.contains(".jar")) {// running in jar
    			
    		}
    	}
    }
    
}
