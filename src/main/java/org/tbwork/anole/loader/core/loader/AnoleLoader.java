package org.tbwork.anole.loader.core.loader;

import java.util.Map;

import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;


public interface AnoleLoader {
	/**
	 * Load "*.anole" files under the class-path directories.
	 * <p><b>Note:</b> Default log level is INFO.
	 */
	public Map<String,FileLoadStatus> load(); 
	/**
	 * Load multiple configuration files. 
	 */
	public Map<String,FileLoadStatus> load(String ... configLocations);
 
}
