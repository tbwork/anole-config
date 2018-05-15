package org.tbwork.anole.loader.context.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tbwork.anole.loader.context.AnoleConfigContext;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.FileUtil;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

/**
 * Configuration context which is loaded from files under the classpath.
 * @author tbwork
 */
public class AnoleClasspathConfigContext implements AnoleConfigContext{

	private Map<String, Boolean> alreadyFoundOrMatchedMap;
	
	public AnoleClasspathConfigContext(LogLevel logLevel, String ... configLocations) {
		AnoleLoader anoleLoader = new AnoleClasspathLoader();
		String [] slashProcessedPathes = FileUtil.format2SlashPathes(configLocations);
		initializeAlreadyFoundMap(slashProcessedPathes);
		Map<String,FileLoadStatus> loadResult = anoleLoader.load(logLevel, slashProcessedPathes);
		checkNotExist(loadResult); 
	} 
	 
	
	public AnoleClasspathConfigContext(LogLevel logLevel) {
		this(logLevel, "*.anole");
	}
	
	public AnoleClasspathConfigContext(String ... configLocations) {
		this(AnoleLogger.defaultLogLevel);
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
			for(Entry<String, FileLoadStatus> entry : loadResult.entrySet()) {
				String absolutePath = entry.getKey();
				if(absolutePath.endsWith(relativePath) && entry.getValue().equals(FileLoadStatus.SUCCESS)) {
					entry2.setValue(true);
				} 
			} 
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
