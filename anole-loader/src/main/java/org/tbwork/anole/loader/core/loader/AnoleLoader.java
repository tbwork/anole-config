package org.tbwork.anole.loader.core.loader;

import java.util.Map;

import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;


public interface AnoleLoader {
	/**
	 * Load "*.anole" files under the class-path directories.
	 * <p><b>Note:</b> Default log level is INFO.
	 */
	public void load();
	/**
	 * Load multiple configuration files. 
	 */
	public void load(String ... configLocations);
 
}
