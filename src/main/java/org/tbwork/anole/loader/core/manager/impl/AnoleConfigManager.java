package org.tbwork.anole.loader.core.manager.impl;

import com.lmax.disruptor.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.expression.ExpressionResolver;
import org.tbwork.anole.loader.core.manager.expression.ExpressionResolverFactory;
import org.tbwork.anole.loader.core.manager.monitor.impl.ConfigChangeMonitor;
import org.tbwork.anole.loader.core.manager.remote.RemoteRetriever;
import org.tbwork.anole.loader.core.manager.updator.impl.AnoleConfigUpdater;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.exceptions.CircularDependencyException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.exceptions.NotReadyException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleValueUtil;
import org.tbwork.anole.loader.util.StringUtil;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * AnoleConfigManager is the heart of anole-loader, it manages all configs.
 * @see #registerConfigItemDefinition(String, String)
 * @see #getConfigItem(String)
 * @author Tommy.Tang
 */
public class AnoleConfigManager implements ConfigManager{

	private static final AnoleLogger logger = new AnoleLogger(AnoleConfigManager.class);
	
	private static final Map<String, ConfigItem> configDefinitionMap = new ConcurrentHashMap<String, ConfigItem>();
	
	private static final Set<String> unknownConfigSet = new HashSet<String>();

	private List<RemoteRetriever> remoteRetrieverList = new ArrayList<>();

	private AnoleConfigUpdater anoleConfigUpdater;

	private static final ConfigManager INSTANCE = new AnoleConfigManager();

	public static ConfigManager getInstance(){
		return INSTANCE;
	}

	@Override
	public void batchRegisterDefinition(List<RawKV> rawKVList) {
		for(RawKV rawKV : rawKVList){
			registerConfigItemDefinition(rawKV.getKey(), rawKV.getValue());
		}
	}


	@Override
	public void registerAndSetValue(String key, String definition) {
		ConfigItem configItem = registerConfigItemDefinition(key, definition);


		if(AnoleValueUtil.containVariable(value, key)){
			value = rsc(key);
			configItem.setValue(value);
		}
	}

	@Override
	public void refresh() {

		batchResolveVariablesAndSetValueExpression();

		calculateExpressionForAllConfigs();

		cleanEscapeCharacters();

		if(!unknownConfigSet.isEmpty()){
			logger.error("There are still some configurations could not be parsed rightly, they are: {} ", unknownConfigSet.toArray().toString() );
		}

	}

	@Override
	public void addRemoteRetriever(RemoteRetriever remoteRetriever) {
		remoteRetrieverList.add(remoteRetriever);
		if(anoleConfigUpdater == null){
			throw new NotReadyException("configUpdater component");
		}
		remoteRetriever.registerMonitor(new ConfigChangeMonitor(anoleConfigUpdater));
	}

	@Override
	public void startUpdater() {
		anoleConfigUpdater = new AnoleConfigUpdater(new AnoleUpdateEventHandler(this));
		anoleConfigUpdater.start();
	}

	@Override
	public void applyChange(String key, String newValue) {
		anoleConfigUpdater.publishEvent(new UpdateEvent(key, newValue));
	}


	@Override
	public ConfigItem getConfigItem(String key){
		//local first
		ConfigItem result =  configDefinitionMap.get(key);
		
		//then system
		if(result == null ) {
			String sysProperty = System.getProperty(key);
			if(sysProperty != null) {
				result = initialConfig(key);
				result.setValue(sysProperty);
			}
		} 
		return result;
	}


	/**
	 * Event handler used to process update events.
	 */
	public static class AnoleUpdateEventHandler implements EventHandler<UpdateEvent> {

		private ConfigManager anoleConfigManager;

		public AnoleUpdateEventHandler(ConfigManager anoleConfigManager){
			this.anoleConfigManager = anoleConfigManager;
		}

		@Override
		public void onEvent(UpdateEvent event, long sequence, boolean endOfBatch) throws Exception {

			ConfigItem configItem =  anoleConfigManager.getConfigItem(event.getKey());
			String oldDefinition = configItem.getDefinition();

			if(oldDefinition == null &&event.getNewValue() == null){
				return ;
			}

			if(oldDefinition != null && oldDefinition.equals(event.getNewValue())){
				return ;
			}

			if(configItem != null){
				configItem.setValue(event.getNewValue());
				logger.info("The key named '{}' changed from '{}' to '{}'", event.getKey(), oldDefinition, event.getNewValue());
			}
		}
	}



	/**
	 * Register the config definition.
	 *
	 * @param key the key of the configuration item.
	 * @param valueDefinition the value definition of the config.
	 * @return the configuration item.
	 */
	private ConfigItem registerConfigItemDefinition(String key, String valueDefinition){
		ConfigItem cItem = configDefinitionMap.get(key);
		String operation = "New";
		if(cItem == null)
			cItem = initialConfig(key);
		else
			operation = "Update";
		if( logger.isDebugEnabled()){
			logger.debug("{} config found: key = {},  definition = {}", operation, key, valueDefinition);
		}
		String oldDefinition = cItem.getDefinition();
		cItem.setDefinition(valueDefinition);
		refreshReferenceRelationShip(key, oldDefinition, valueDefinition);
		return cItem;
	}



    /**
     * <p> Replace all variables of all config items with corresponding values.
	 * For those configs containing variables, those variables will be replaced
	 * with concrete values recursively.</p>
     * E.g., a snippet of Anole configuration is as following:
     * <pre>
     * ip=127.0.0.1
     * port=80
     * connectionString=#{ip}:#{port}
     * </pre>
     * In this case, after calling this method, the connectionString 
     * would be 127.0.0.1:80
     */
    private void batchResolveVariablesAndSetValueExpression(){
    	Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
    	// process definitions without variables
		for(Entry<String,ConfigItem> item : entrySet){
			ConfigItem configItem = item.getValue();
			if(!AnoleValueUtil.containVariable(configItem.getDefinition(), item.getKey()) ){
				// only for those not having-variable properties.
				configItem.setValueExpression(configItem.getDefinition());
			}
		}
		// process definitions with variables
    	for(Entry<String,ConfigItem> item : entrySet){
    		ConfigItem configItem = item.getValue();
    		if(AnoleValueUtil.containVariable(configItem.getDefinition(), item.getKey())){
    			// only for those having-variable properties.
				configItem.setValueExpression(rsc(item.getKey()));
			}
    	}
    }



	/**
	 * Calculate the expression for all config items.
	 * Example 1:
	 * <pre>
	 *     a = true ? 123 : 345
	 * </pre>
	 * a's value would be 123
	 *
	 * Example 2:
	 * <pre>
	 *     a = 345
	 * </pre>
	 * a's value would be 345 (it is not an expression but a plain value.)
	 *
	 */
    private void calculateExpressionForAllConfigs(){
		Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
		for(Entry<String,ConfigItem> item : entrySet){
			ConfigItem configItem = item.getValue();
			configItem.setValue(
			 	calculateExpressionForAllConfigs(configItem.getKey(), configItem.getValueExpression())
			);
		}
	}

	/**
	 * Calculate expression for the given ownerKey.
	 *
	 * @param ownerKey the key owns the given expression
	 * @param expression the expression
	 * @return the calculated result
	 */
	private String calculateExpressionForAllConfigs(String ownerKey, String expression){
		if(AnoleValueUtil.isExpression(expression)){
			ExpressionResolver expressionResolver = ExpressionResolverFactory.findSuitableExpressionResolver(expression);
			String calculateResult = expressionResolver.resolve(ownerKey, expression);
			return calculateResult;
		}
		return expression;
	}

    /**
     * Clear all escape characters in the configuration value.
     */
    private void cleanEscapeCharacters(){
    	Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
    	for(Entry<String,ConfigItem> item : entrySet){
			String strValue = item.getValue().strValue();
			item.getValue().setValue(StringUtil.replaceEscapeChars(strValue));
    	}
    }



    @Data
	@AllArgsConstructor
    static class ParseResult{
    	private String expression;
    	private String error;
	}

	/**
	 * Recursively build all properties.
	 *
	 * @param key the config key
	 * @param definition definition
	 * @return the value expression
	 */
	private ParseResult parseDefinitionToExpression(String key, String definition){
		if(unknownConfigSet.contains(key)){
			throw new CircularDependencyException(key);
		}
		unknownConfigSet.add(key);
		String resultExpression = definition;
		String resultError = null;
		String [] variablesWithCloth = AnoleValueUtil.getVariables(definition, key);

		for(String str: variablesWithCloth){
			String vkey = AnoleValueUtil.getVariable(str);
			if(StringUtil.isNullOrEmpty(vkey)){
				throw new ErrorSyntaxException(key, str + " must contains a valid variable.");
			}
			ConfigItem vConfig = extendibleGetConfigItem(vkey);
			if(vConfig == null || vConfig.strValue() == null) {
				String errorMessage = String.format("There is no manual-set or default-set value for %s", vkey);
				if(isRunInStrictMode()){
					throw new ErrorSyntaxException(key, errorMessage);
				}
				else{
					resultError = errorMessage;
				}
			}
			else{
				// vKey found
				// get value expression
				if(AnoleValueUtil.containVariable(vConfig.getDefinition(), vkey)){
					ParseResult parseResult = parseDefinitionToExpression(vkey, vConfig.getDefinition());
					vConfig.setValueExpression(parseResult.getExpression());
					vConfig.setError(parseResult.getError());
				}
			}

			resultExpression = resultExpression.replace(str, vConfig.getValueExpression());


			String realValue = parseDefinition(vkey, );
			if(realValue == null){
				String message = String.format("The config (key=%s) could not be null value because it is a reference (dependency) of config(key=%s)", vkey, key);
				throw new ErrorSyntaxException(key, message);
			}
			if(!resultValue.equals(str)){
				resultValue = resultValue.replace(str, realValue);
			}

		}
		ci.setValue(resultValue);
		unknownConfigSet.remove(key);
		// else: real value is still not found, keep intact and do nothing
		return ci.strValue();
	}


	private boolean isRunInStrictMode(){
		ConfigItem configItem = getConfigItem("anole.mode");
		if(configItem != null){
			return "strict".equals(configItem.getDefinition());
		}
		return true;
	}

	/**
	 * Recursively build all properties.
	 *
	 * @param key the key
	 * @return
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
		String [] variablesWithCloth = AnoleValueUtil.getVariables(ci.getDefinition(), key);
    	String resultValue = ci.getDefinition();
		for(String str: variablesWithCloth){
			String vkey = AnoleValueUtil.getVariable(str);
			if(vkey==null || vkey.isEmpty())
				throw new ErrorSyntaxException(key, str + " must contains a valid variable.");
			String realValue = rsc(vkey);
			if(realValue == null){
				String message = String.format("The config (key=%s) could not be null value because it is a reference (dependency) of config(key=%s)", vkey, key);
				throw new ErrorSyntaxException(key, message);
			} 
			if(!resultValue.equals(str)){
				resultValue = resultValue.replace(str, realValue);
			}

		}
		ci.setValue(resultValue);
		unknownConfigSet.remove(key);
		// else: real value is still not found, keep intact and do nothing
    	return ci.strValue(); 
    }

	/**
	 *
	 * Refresh the reference relationship of the specified key when its value changed. <br/>
	 * E.g., when a's value changed from "${b}+${c}" to "${b}+${d}", 'a' will be removed from c's
	 * referenced set (referencedMap.get('c')) and added into d's referenced set.
	 * @param key the config key
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
    private void refreshReferenceRelationShip(String key, String oldValue, String newValue){

    	if(oldValue == null && newValue == null){
    		return;
		}

    	if(oldValue != null && oldValue.equals(newValue)){
    		return;
		}

		Set<String> oldReferenceSet = new HashSet<>();
		if(StringUtil.isNotEmpty(oldValue)){
			oldReferenceSet.addAll(Arrays.asList(AnoleValueUtil.getVariables(oldValue, key)));
		}

		Set<String> newReferenceSet = new HashSet<>();
		if(StringUtil.isNotEmpty(newValue)){
			newReferenceSet.addAll(Arrays.asList(AnoleValueUtil.getVariables(newValue, key)));
		}

		Iterator<String> it = oldReferenceSet.iterator();

		for(int i = 0 ; i < oldReferenceSet.size(); i++){
			String keyName = it.next();
			if(newReferenceSet.contains(keyName)){
				it.remove();
				newReferenceSet.remove(keyName);
				i--;
			}
		}


		for(String variableKey : oldReferenceSet){
			if(newReferenceSet.contains(variableKey)){
				continue;
			}
			else{
				// Dumb but clear :)
				// means the referenced variable is removed from current value
				removeReferenceRelationShip(variableKey, key);
			}
		}

		for(String variableKey : newReferenceSet){
			if(oldReferenceSet.contains(variableKey)){
				continue;
			}
			else{
				// Dumb but clear :)
				// means the variableKey is referenced by the current key
				recordReferenceRelationShip(variableKey, key);
			}
		}

	}


	/**
	 * Record the reference relationship between two keys.
	 * E.g.,
	 * <pre>
	 *      a = ${b}
	 * <pre/>
	 * Then, 'b' is the variable key and 'a' is the container key.
	 *
	 * @param variableKey  the variable key
	 * @param parentKey the container key, also called parent key
	 */
    private void recordReferenceRelationShip(String variableKey, String parentKey){

		Set<String> parentConfigKeys = getConfigItem(variableKey).getParentConfigKeys();
    	if(parentConfigKeys == null){
			parentConfigKeys = new HashSet<>();
		}
    	if(parentConfigKeys.contains(variableKey)){
    		throw new CircularDependencyException(variableKey);
		}
		parentConfigKeys.add(parentKey);

	}

	/**
	 * Remove the reference relationship between two keys.
	 * E.g.,
	 * <pre>
	 *      a = ${b}
	 * <pre/>
	 * Then, 'b' is the variable key and 'a' is the container key.
	 * After this operation, the containerKey will be removed from
	 * variableKey's referenced set.
	 * @param variableKey  the variable key
	 * @param parentKey the container key, also called parent key
	 */
	private void removeReferenceRelationShip(String variableKey, String parentKey){

		Set<String> parentConfigKeys = getConfigItem(variableKey).getParentConfigKeys();
		if(parentConfigKeys == null){
			return;
		}
		if(parentConfigKeys.contains(parentKey)){
			parentConfigKeys.remove(parentKey);
		}
	}


    /**
     * Retrieve config from the local config file or the remote config servers.
     * @param key the key 
     */
    protected ConfigItem extendibleGetConfigItem(String key){
		ConfigItem configItem = configDefinitionMap.get(key);
		if(configItem != null){
			return configItem;
		}
		for(RemoteRetriever remoteRetriever : remoteRetrieverList){
			String remoteValue = remoteRetriever.retrieve(key);
			if(StringUtil.isNotEmpty(remoteValue)){
				ConfigItem registerResult = registerConfigItemDefinition(key, remoteValue);
				logger.info("Retrieving value (definition) of '{}' from {} (remote) successfully", key, remoteRetriever.getName());
				return registerResult;
			}
		}
    	return null;
    }

	private ConfigItem initialConfig(String key){
		key = key.trim();
		ConfigItem cItem = new ConfigItem(key);
		configDefinitionMap.put(key, cItem);
		return cItem;
	}

}
