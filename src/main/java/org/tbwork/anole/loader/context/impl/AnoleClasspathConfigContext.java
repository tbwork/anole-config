package org.tbwork.anole.loader.context.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;
import org.tbwork.anole.loader.util.FileUtil;

/**
 * <p>Before using Anole to manage your configuration, 
 * you should create the configuration context. It is
 * recommended to use the {@link AnoleConfigLocation} to
 * setup your application. However in some case you may 
 * need to create the configuration context manually.
 * And this is a way to load class-path property files.
 * <p>Usage example:
 *    	
 * <pre>
 *    AnoleClasspathConfigContext acc = new AnoleClasspathConfigContext(LogLevel.INFO, configFilePath);
 *    //use Anole as you like.
 * </pre>
 * <p> <b>About LogLevel:</b> The anole does not use any log implement
 * in the startup stage, it only providers the standard output to the
 * console window. When the application started, it use SLF4J facade to 
 * print logs. 
 * @author tbwork
 * @see AnoleFileConfigContext
 */
public class AnoleClasspathConfigContext{

	private Map<String, Boolean> alreadyFoundOrMatchedMap;
	
	public AnoleClasspathConfigContext(String ... configLocations) {
		AnoleLoader anoleLoader = new AnoleClasspathLoader();
		String [] slashProcessedPathes = FileUtil.format2SlashPathes(configLocations);
		initializeAlreadyFoundMap(slashProcessedPathes); 
		Map<String,FileLoadStatus> loadResult = anoleLoader.load(slashProcessedPathes);
		checkNotExist(loadResult); 
	} 
	 
	
	public AnoleClasspathConfigContext() {
		this("*.anole");
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
