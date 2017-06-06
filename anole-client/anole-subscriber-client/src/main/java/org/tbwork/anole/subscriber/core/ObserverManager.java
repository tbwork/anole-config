package org.tbwork.anole.subscriber.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
 
public class ObserverManager {

	private static final ObserverManager om = new ObserverManager() ;
	
	private static final Logger logger = LoggerFactory.getLogger(ObserverManager.class);
	
	private Map<String, List<ConfigObserver>> preObservers  = new ConcurrentHashMap<String, List<ConfigObserver>>();
	private Map<String, List<ConfigObserver>> postObservers = new ConcurrentHashMap<String, List<ConfigObserver>>();
	
	private ObserverManager(){}
	
	public static ObserverManager instance(){
		return om;
	}
	
	public void addPreObservers(String key, ConfigObserver ... pco){
		if(logger.isDebugEnabled())
			logger.debug("[:)] An pre-value-set observer is added to the key (={})", key); 
		addObservers(preObservers, key, pco); 
	}
	
	public void addPostObservers(String key, ConfigObserver ... pco){
		if(logger.isDebugEnabled())
			logger.debug("[:)] An post-value-set observer is added to the key (={})", key);
		addObservers(postObservers, key, pco); 
	}
	 
	public void removePreObservers(String key){
		if(logger.isDebugEnabled())
			logger.debug("[:)] Pre-value-set observers of the key (={}) are removed", key);
		preObservers.remove(key); 
	}
	
	public void removePostObservers(String key){
		if(logger.isDebugEnabled())
			logger.debug("[:)] Post-value-set observers of the key (={}) are removed", key);
		postObservers.remove(key); 
	}
	
	public List<ConfigObserver> getPreObservers(String key){
		 return preObservers.get(key);
	}
	
	public List<ConfigObserver> getPostObservers(String key){
		 return postObservers.get(key);
	}
	
	private void addObservers(Map<String, List<ConfigObserver>> obs, String key, ConfigObserver...cob){
		List<ConfigObserver> tempObs = null;
		AnoleClient.getProperty(key);
		if(!obs.containsKey(key) || obs.get(key) == null )
		   tempObs = new ArrayList<ConfigObserver>();  
		else
		   tempObs = obs.get(key);
		for(ConfigObserver item : cob)
		   tempObs.add(item);
	} 
	 
}
