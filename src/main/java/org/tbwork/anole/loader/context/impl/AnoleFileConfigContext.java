package org.tbwork.anole.loader.context.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.AnoleConfigContext;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleFileLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;
import org.tbwork.anole.loader.util.FileUtil;


public class AnoleFileConfigContext implements AnoleConfigContext{
  
	private Map<String, Boolean> alreadyFoundOrMatchedMap;
	
	public AnoleFileConfigContext(LogLevel logLevel, String ... configLocations) {
		AnoleLoader anoleLoader = new AnoleFileLoader();
		String [] slashProcessedPathes = FileUtil.format2SlashPathes(configLocations);
		initializeAlreadyFoundMap(slashProcessedPathes);
		Map<String,FileLoadStatus> loadResult = anoleLoader.load(logLevel, slashProcessedPathes);
		checkNotExist(loadResult); 
	}
	
	public AnoleFileConfigContext(String ... configLocations) {
		this(AnoleLogger.defaultLogLevel, configLocations);
	}
	
	private void initializeAlreadyFoundMap(String ... configLocations) {
		if(alreadyFoundOrMatchedMap == null)
			alreadyFoundOrMatchedMap = new HashMap<String,Boolean>(); 
		for(String configLocation : configLocations) {
			configLocation = FileUtil.format2Slash(configLocation);
			alreadyFoundOrMatchedMap.put(configLocation, false); 
		}
	}
	
	private void checkNotExist(Map<String,FileLoadStatus> loadResult) {
		for(Entry<String, Boolean> entry2 : alreadyFoundOrMatchedMap.entrySet()) {
			String relativePath = entry2.getKey();
			FileLoadStatus fls = loadResult.get(relativePath);
			if(fls != null && fls.equals(FileLoadStatus.SUCCESS))
				entry2.setValue(true); 
		}
		for(Entry<String, Boolean> entry2 : alreadyFoundOrMatchedMap.entrySet()) {
			String relativePath = entry2.getKey();
			if(!entry2.getValue()) {
				if(relativePath.contains("*")) { //In terms of asterisk path, it is no need to throw an exception. 
					AnoleLogger.warn("There is no matched file for '{}'", relativePath);
				}
				else {
					throw new ConfigFileDirectoryNotExistException(relativePath);
				}
			}
		}
	}
}
