package org.tbwork.anole.loader.core.manager.impl;

import lombok.Data;
import org.tbwork.anole.loader.Anole;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.expression.ExpressionResolver;
import org.tbwork.anole.loader.core.manager.expression.ExpressionResolverFactory;
import org.tbwork.anole.loader.core.manager.modhub.impl.AnoleOutgoConfigUpdateManager;
import org.tbwork.anole.loader.core.manager.monitor.impl.ConfigChangeRemoteMonitor;
import org.tbwork.anole.loader.core.manager.source.RemoteRetriever;
import org.tbwork.anole.loader.core.manager.source.SourceRetriever;
import org.tbwork.anole.loader.core.manager.modhub.ConfigUpdateManager;
import org.tbwork.anole.loader.core.manager.modhub.impl.AnoleIncomeConfigUpdateManager;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.exceptions.CircularDependencyException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.exceptions.NotReadyException;
import org.tbwork.anole.loader.statics.BuiltInConfigKeyBook;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleValueUtil;
import org.tbwork.anole.loader.util.StringUtil;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AnoleConfigManager is the heart of anole-loader, it manages all configs.
 * @see #registerConfigItemDefinition(String, String)
 * @see #getConfigItem(String)
 * @author Tommy.Tang
 */
public class AnoleConfigManager implements ConfigManager{

	private static final AnoleLogger logger = new AnoleLogger(AnoleConfigManager.class);
	
	private static final Map<String, ConfigItem> configDefinitionMap = new ConcurrentHashMap<String, ConfigItem>();

	private List<SourceRetriever> extensionSources = new ArrayList<>();

	/**
	 * Store keys to system property manger temporarily.
	 */
	private Set<String> tempStoreInSystemKeySet = new HashSet<>();

	private ConfigUpdateManager anoleIncomeConfigUpdater;

	private ConfigUpdateManager anoleOutgoConfigUpdater;

	private static final ConfigManager INSTANCE = new AnoleConfigManager();

	public static ConfigManager getInstance(){
		return INSTANCE;
	}

	private volatile boolean needCheckIntegrity = false;

	@Override
	public void batchRegisterDefinition(List<RawKV> rawKVList) {
		for(RawKV rawKV : rawKVList){
			registerConfigItemDefinition(rawKV.getKey(), rawKV.getValue());
		}
	}


	@Override
	public ConfigItem registerAndSetValue(String key, String definition) {
		return registerAndSetValue(key, definition, System.currentTimeMillis());
	}

	@Override
	public ConfigItem registerFromAnywhere(String key) {
		ConfigItem configItem = extendibleGetConfigItem(key);
		return configItem;
	}

	@Override
	public ConfigItem registerAndSetValue(String key, String definition, long updateTime) {
		ConfigItem configItem = registerConfigItemDefinition(key, definition);
		if(AnoleValueUtil.containVariable(definition)){
			parseDefinitionAndCalculateValue(configItem);
		}
		else {
			setConfigValue(configItem, calculateExpression(key, definition));
		}
		configItem.setLastUpdateTime(updateTime);
		if(Anole.initialized){
			// means any change could cause other related changes as a chain reaction.
			processChainReaction(key);
		}
		return configItem;
	}

	@Override
	public void registerToSystem() {
		Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
		// process definitions without variables
		for(Entry<String,ConfigItem> item : entrySet){
			ConfigItem configItem = item.getValue();
			if( configItem.strValue() != null){
				if( System.getProperty(configItem.getKey()) == null){
					// means these configs will be removed after usage.
					tempStoreInSystemKeySet.add(configItem.getKey());
				}
				System.setProperty(configItem.getKey(), configItem.strValue());
			}
		}
	}

	@Override
	public void removeFromSystem() {
		Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
		Iterator<String> iterator = tempStoreInSystemKeySet.iterator();
		while(iterator.hasNext()){
			System.clearProperty(iterator.next());
			iterator.remove();
		}
	}

	@Override
	public boolean interest(String key) {
		return configDefinitionMap.containsKey(key);
	}

	@Override
	public void refresh(boolean needCheckIntegrity) {

		this.needCheckIntegrity = needCheckIntegrity;

		calculateValueForNotPlainConfigs();

		cleanEscapeCharacters();

		if( needCheckIntegrity ){
			Set<String> unresolvedKeySet = getUnresolvedKeySet();
			if(!unresolvedKeySet.isEmpty()){
				logger.error("There are still some configurations could not be parsed rightly, they are: {} ", unresolvedKeySet.toArray().toString() );
			}
		}

	}



	@Override
	public void addExtensionRetriever(SourceRetriever sourceRetriever) {
		extensionSources.add(sourceRetriever);
		if(anoleIncomeConfigUpdater == null){
			throw new NotReadyException("configUpdater component");
		}
		if(sourceRetriever instanceof RemoteRetriever){
			((RemoteRetriever)sourceRetriever).registerMonitor(new ConfigChangeRemoteMonitor(anoleIncomeConfigUpdater, this));
		}
	}

	@Override
	public void startReceiveIncomeUpdates() {
		anoleIncomeConfigUpdater = new AnoleIncomeConfigUpdateManager();
		anoleIncomeConfigUpdater.startRecord();
	}

	@Override
	public void startProcessIncomeUpdates() {
		anoleIncomeConfigUpdater.startProcess();
	}

	@Override
	public void startReceiveOutgoUpdates() {
		anoleOutgoConfigUpdater = new AnoleOutgoConfigUpdateManager();
		anoleOutgoConfigUpdater.startRecord();
	}

	@Override
	public void startProcessOutgoUpdates() {
		anoleOutgoConfigUpdater.startProcess();
	}

	@Override
	public void shutDown() {
		anoleIncomeConfigUpdater.shutDown();
		anoleOutgoConfigUpdater.shutDown();
	}

	@Override
	public void submitIncomeUpdate(String key, String newValue) {
		anoleIncomeConfigUpdater.publishEvent(new UpdateEvent(key, newValue));
	}

	@Override
	public void submitOutgoUpdate(String key, String newValue) {
		anoleOutgoConfigUpdater.publishEvent(new UpdateEvent(key, newValue));
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
				registerAndSetValue(key, sysProperty);
			}
		}

		// then environment
		if(result == null) {
			String envProperty = System.getenv(key);
			if(envProperty != null) {
				result = initialConfig(key);
				registerAndSetValue(key, envProperty);
			}
		}

		return result;
	}



	/**
	 * Calculate value of each config item containing other variables, according to the definition.
	 */
	private void calculateValueForNotPlainConfigs(){
		Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
		// process definitions without variables
		for(Entry<String,ConfigItem> item : entrySet){
			ConfigItem configItem = item.getValue();
			if(AnoleValueUtil.containVariable(configItem.getDefinition()) ){
				// contains variables
				parseDefinitionAndCalculateValue(configItem);
			}
		}
	}

	/**
	 * Get all unresolved key as a set.
	 * @return the key set.
	 */
	private Set<String> getUnresolvedKeySet(){
		Set<String> unresolvedSet = new HashSet<>();
		Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
		// process definitions without variables
		for(Entry<String,ConfigItem> item : entrySet){
			ConfigItem configItem = item.getValue();
			if(AnoleValueUtil.containVariable(configItem.getDefinition()) && configItem.strValue() == null){
				unresolvedSet.add(configItem.getKey());
			}
		}
		return unresolvedSet;
	}

	/**
	 * Register the config definition and set value for configs if the definition
	 * did not contain any variable and was not an expression.
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
		// for plain value without variables, set value immediately
		if(!AnoleValueUtil.containVariable(valueDefinition)){
			String value = valueDefinition;
			if(AnoleValueUtil.isExpression(valueDefinition)){
				value = calculateExpression(key, AnoleValueUtil.getExpression(valueDefinition));
			}
			setConfigValue(cItem, value);
		}
		refreshReferenceRelationShip(key, oldDefinition, valueDefinition);
		return cItem;
	}

	/**
	 * Calculate expression for the given ownerKey.
	 *
	 * @param ownerKey the key owns the given expression
	 * @param expression the expression
	 * @return the calculated result
	 */
	private String calculateExpression(String ownerKey, String expression){
		if(AnoleValueUtil.isExpression(expression)){
			ExpressionResolver expressionResolver = ExpressionResolverFactory.findSuitableExpressionResolver(expression);
			String calculateResult = expressionResolver.resolve(ownerKey, AnoleValueUtil.getExpression(expression));
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
    		if(StringUtil.isNotEmpty(item.getValue().strValue())){
				String strValue = item.getValue().strValue();
				setConfigValue(item.getValue(), StringUtil.replaceEscapeChars(strValue));
			}
    	}
    }


	/**
	 * Refresh all affected configs' value due to update of the config with the given orgKey.
	 *
	 * @param orgKey the modified config's key
	 */
    private void processChainReaction(String orgKey){
    	topologySortAndRefreshValue(orgKey);
	}


	@Data
	static class GraphNode{

    	private Set<String> referenceNodeKeys;

    	private String key;

    	public GraphNode(String key){
    		this.key = key;
    		this.referenceNodeKeys = new HashSet<>();
		}

	}

	/**
	 * <p>The configs' dependency relationship is a Directed Acyclic Graph, and
	 * how to sort the update order is a topological sort algorithm.<p/>
	 *
	 * Access <a href="https://algorithmist.com/wiki/Topological_sort">Topological sort</a>
	 * for further read.
	 * @param startKey the start config's key
	 */
	private void topologySortAndRefreshValue(String startKey){

		// build the DAG
		Map<String, GraphNode> relatedKeyMap = new HashMap<>();
		relatedKeyMap.put(startKey, new GraphNode(startKey));
		findRelatedKeysByDFS(relatedKeyMap, relatedKeyMap.get(startKey));

		// topological sort
		int lastSize = relatedKeyMap.size();
		while(lastSize > 0){
			Iterator<Entry<String,GraphNode>> iterator = relatedKeyMap.entrySet().iterator();
			String firstKey = null;
			while(iterator.hasNext()){
				Entry<String,GraphNode> node = iterator.next();
				if(firstKey == null){
					firstKey = node.getKey();
				}
				if(node.getValue().referenceNodeKeys.isEmpty()){
					// process the node without referencing other nodes.
					parseDefinitionAndCalculateValue(getConfigItem(node.getKey()));
					iterator.remove();
					// remove the node from its parent nodes' referenceNodes set.
					for(String parentKey : getConfigItem(node.getKey()).getParentConfigKeys()){
						relatedKeyMap.get(parentKey).referenceNodeKeys.remove(node.getKey());
					}
				}
			}
			if(lastSize == relatedKeyMap.size()){
				StringBuilder stringBuilder = new StringBuilder();
				iterator = relatedKeyMap.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<String,GraphNode> node = iterator.next();
					stringBuilder.append(node.getKey()).append("  --->  ");
					stringBuilder.append(StringUtil.join(",", node.getValue().getReferenceNodeKeys().stream().collect(Collectors.toList())));
					stringBuilder.append("\n");
				}
				logger.error("Dependency loop is found, current relationships are >>>>>>>>>>>>> \n {}", stringBuilder.toString());
				throw new CircularDependencyException(firstKey);
			}
			lastSize = relatedKeyMap.size();
		}
	}


	private void findRelatedKeysByDFS(Map<String, GraphNode> relatedKeyMap, GraphNode startNode){
		for(String parentKey : getConfigItem(startNode.getKey()).getParentConfigKeys()){
			if(!relatedKeyMap.containsKey(parentKey)){
				relatedKeyMap.put(parentKey, new GraphNode(parentKey));
			}
			relatedKeyMap.get(parentKey).referenceNodeKeys.add(startNode.getKey());

			findRelatedKeysByDFS(relatedKeyMap,  relatedKeyMap.get(parentKey));
		}
	}


	/**
	 * @see {@link #parseDefinitionAndCalculateValue(ConfigItem, Set)}
	 * @param configItem the config item.
	 */
	private void parseDefinitionAndCalculateValue(ConfigItem configItem){
		parseDefinitionAndCalculateValue(configItem, new HashSet<String>());
	}

	/**
	 * Recursively parse config's definition and calculate the value.
	 *
	 * @param configItem the config item
	 * @param unknownConfigSet the config key set which are not resolved yet.
	 */
	private void parseDefinitionAndCalculateValue(ConfigItem configItem, Set<String> unknownConfigSet){
		if(unknownConfigSet.contains(configItem.getKey())){
			throw new CircularDependencyException(configItem.getKey());
		}
		unknownConfigSet.add(configItem.getKey());

		String [] variablesWithCloth = AnoleValueUtil.getVariablesWithCloth(configItem.getDefinition(), configItem.getKey());

		String resolvedValue = configItem.getDefinition();
		for(String str: variablesWithCloth){

			String vkey = AnoleValueUtil.getVariable(str, configItem.getKey());

			ConfigItem vConfig = extendibleGetConfigItem(vkey);

			if(vConfig == null ) {
				if(!needCheckIntegrity){
					return;
				}
				String errorMessage = String.format("There is no manual-set or default-set value for '%s'.", vkey);
				if(isRunInStrictMode()){
					throw new ErrorSyntaxException(vkey, errorMessage);
				}
				else{
					configItem.setError(errorMessage);
					return;
				}
			}

			if(Anole.initialized && Anole.getBoolProperty(BuiltInConfigKeyBook.FORCE_REFRESH_REFERENCING_KEYS, true)){
				// force to refresh the referencing configs.
				if(AnoleValueUtil.containVariable(vConfig.getDefinition())){
					parseDefinitionAndCalculateValue(vConfig, unknownConfigSet);
				}
			}

			if(vConfig.strValue() == null){
				configItem.setError(vConfig.getError());
			}

			// variable's value is ready
			resolvedValue = resolvedValue.replace(str, vConfig.strValue());
		}
		// process expression
		if(AnoleValueUtil.isExpression(resolvedValue)){
			resolvedValue = calculateExpression(configItem.getKey(), resolvedValue);
		}
		setConfigValue(configItem, resolvedValue);
		unknownConfigSet.remove(configItem.getKey());
	}


	private boolean isRunInStrictMode(){
		ConfigItem configItem = getConfigItem("anole.mode");
		if(configItem != null){
			return "strict".equals(configItem.getDefinition());
		}
		return true;
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
		if(StringUtil.isNotEmpty(oldValue) && AnoleValueUtil.containVariable(oldValue)){
			oldReferenceSet.addAll(Arrays.asList(AnoleValueUtil.getVariables(oldValue, key)));
		}

		Set<String> newReferenceSet = new HashSet<>();
		if(StringUtil.isNotEmpty(newValue) && AnoleValueUtil.containVariable(newValue)){
			newReferenceSet.addAll(Arrays.asList(AnoleValueUtil.getVariables(newValue, key)));
		}

		getConfigItem(key).getChildConfigKeys().addAll(newReferenceSet);

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
		ConfigItem configItem = getConfigItem(variableKey);
		if(configItem == null){
			configItem = initialConfig(variableKey);
		}
		Set<String> parentConfigKeys = configItem.getParentConfigKeys();
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

		ConfigItem configItem = getConfigItem(variableKey);
		if(configItem == null){
			configItem = initialConfig(variableKey);
		}
		Set<String> parentConfigKeys = configItem.getParentConfigKeys();
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
		ConfigItem configItem = getConfigItem(key);
		if(configItem != null && configItem.strValue() != null){
			return configItem;
		}
		for(SourceRetriever extensionRetriever : extensionSources){
			String remoteValue = extensionRetriever.retrieve(key);
			if(StringUtil.isNotEmpty(remoteValue)){
				ConfigItem registerResult = registerAndSetValue(key, remoteValue);
				logger.info("Retrieving value (definition) of '{}' from {} successfully", key, extensionRetriever.getName());
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


	private void setConfigValue(ConfigItem config, String value){
		value = StringUtil.replaceEscapeChars(value);
    	config.setValue(value);
    	if(Anole.initialized){
			this.submitOutgoUpdate(config.getKey(), value);
		}
	}

}
