package org.tbwork.anole.loader.core.manager.impl;

import com.lmax.disruptor.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.tbwork.anole.loader.context.Anole;
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
		registerAndSetValue(key, definition, System.currentTimeMillis());
	}

	@Override
	public void registerAndSetValue(String key, String definition, long updateTime) {
		ConfigItem configItem = registerConfigItemDefinition(key, definition);
		if(AnoleValueUtil.containVariable(definition,key)){
			parseDefinitionAndCalculateValue(configItem);
		}
		else {
			configItem.setValue(calculateExpression(key, definition));
		}
		configItem.setLastUpdateTime(updateTime);
		if(Anole.initialized){
			// means any change could cause other related changes as a chain reaction.
			processChainReaction(key);
		}
	}

	@Override
	public boolean interest(String key) {
		return configDefinitionMap.containsKey(key);
	}

	@Override
	public void refresh() {

		calculateValueForAll();

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
		remoteRetriever.registerMonitor(new ConfigChangeMonitor(anoleConfigUpdater, this));
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

			if(oldDefinition == null && event.getNewValue() == null){
				return ;
			}

			if(oldDefinition != null && oldDefinition.equals(event.getNewValue())){
				return ;
			}

			if(configItem.getLastUpdateTime() > event.getCreateTime()){
				return ; // ignore old version update
			}

			anoleConfigManager.registerAndSetValue(event.getKey(), event.getNewValue(), event.getCreateTime());

			if(configItem != null){
				configItem.setValue(event.getNewValue());
				logger.info("The key named '{}' changed from '{}' to '{}'", event.getKey(), oldDefinition, event.getNewValue());
			}
		}
	}


	/**
	 * Calculate value of each config item, according to the definition.
	 */
	private void calculateValueForAll(){
		Set<Entry<String,ConfigItem>> entrySet = configDefinitionMap.entrySet();
		// process definitions without variables
		for(Entry<String,ConfigItem> item : entrySet){
			ConfigItem configItem = item.getValue();
			if(AnoleValueUtil.containVariable(configItem.getDefinition(), item.getKey()) ){
				// contains variables
				parseDefinitionAndCalculateValue(configItem);
			}
			else{
				// plain values
				if(!AnoleValueUtil.isExpression(configItem.getDefinition())){
					// not an expression
					configItem.setValue(configItem.getDefinition());
				}
				else{
					String expression = configItem.getDefinition();
					configItem.setValue(calculateExpression(configItem.getKey(), expression));
				}
			}
		}
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


	/**
	 * Refresh all affected configs' value due to update of the config with the given orgKey.
	 *
	 * @param orgKey the modified config's key
	 */
    private void processChainReaction(String orgKey){

    	ConfigItem configItem = getConfigItem(orgKey);

		List<String> affectedKeys = new LinkedList<String>();
		affectedKeys.addAll(configItem.getParentConfigKeys());

		while(!affectedKeys.isEmpty()){

			String affectedKey = affectedKeys.get(0);

			ConfigItem affectedConfigItem = configDefinitionMap.get(affectedKey);

			parseDefinitionAndCalculateValue(affectedConfigItem);

			affectedKeys.addAll(affectedConfigItem.getParentConfigKeys());

			affectedKeys.remove(0);
		}
	}


	@Data
	@AllArgsConstructor
	static class GraphNode{

    	private Set<GraphNode> referenceNodes;

    	private String key;

	}

	/**
	 * <p>The configs' dependency relationship is an unidirectional graph, and
	 * how to sort the update order is a topological sort algorithm.<p/>
	 *
	 * Access <a href="https://algorithmist.com/wiki/Topological_sort">Topological sort</a>
	 * for further read.
	 * @param startKey the start config's key
	 * @return the sorted keys of config which will be updated.
	 */
	private List<String> topologySort(String startKey){

		// build the graph.
		GraphNode root = new GraphNode(null, startKey);

		Map<String, GraphNode> nodeMap = new HashMap<>();
		nodeMap.put(startKey, root);

		Stack<String> waitForProcessNodes = new Stack<String>();

		waitForProcessNodes.push(startKey);

		while(!waitForProcessNodes.empty()){

			String key = waitForProcessNodes.pop();

			for(String parentKey : getConfigItem(startKey).getParentConfigKeys()){
				GraphNode temp = nodeMap.get(parentKey);
				if(temp == null){
					temp = new GraphNode(new HashSet<GraphNode>(), parentKey);
				}
				temp.getReferenceNodes().add(nodeMap.get(key));
				waitForProcessNodes.push(parentKey);
			}

		}

		for(String parentKey : getConfigItem(startKey).getParentConfigKeys()){
			GraphNode temp = new GraphNode(new HashSet<GraphNode>(), parentKey);
			temp.getReferenceNodes().add(root);
		}
		List<String>


	}


	/**
	 * Recursively parse config's definition and calculate the value.
	 *
	 * @param configItem the config item
	 */
	private void parseDefinitionAndCalculateValue(ConfigItem configItem){
		if(unknownConfigSet.contains(configItem.getKey())){
			throw new CircularDependencyException(configItem.getKey());
		}
		unknownConfigSet.add(configItem.getKey());

		String [] variablesWithCloth = AnoleValueUtil.getVariables(configItem.getDefinition(), configItem.getKey());

		String resolvedValue = configItem.getDefinition();
		for(String str: variablesWithCloth){
			String vkey = AnoleValueUtil.getVariable(str);
			if(StringUtil.isNullOrEmpty(vkey)){
				throw new ErrorSyntaxException(configItem.getKey(), str + " must contains a valid variable.");
			}
			ConfigItem vConfig = extendibleGetConfigItem(vkey);

			if(vConfig == null) {
				String errorMessage = String.format("There is no manual-set or default-set value for %s", vkey);
				if(isRunInStrictMode()){
					throw new ErrorSyntaxException(vkey, errorMessage);
				}
				else{
					configItem.setError(errorMessage);
					return;
				}
			}

			if(AnoleValueUtil.containVariable(vConfig.getDefinition(), vConfig.getKey())){
				parseDefinitionAndCalculateValue(vConfig);
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
		configItem.setValue(resolvedValue);
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
