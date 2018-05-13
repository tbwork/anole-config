package org.tbwork.anole.loader.core.loader;

import java.util.Map;

import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;


public interface AnoleLoader {
	/**
	 * Load "*.anole" files within the class-path directories.
	 * <p><b>Note:</b> Default log level is INFO.
	 */
	public Map<String,FileLoadStatus> load(); 
	/**
	 * Load multiple configuration files. 
	 */
	public Map<String,FileLoadStatus> load(String ... configLocations);
 
	/**
	 * <p>You can control the log level of Anole's output in
	 * the startup stage by setting <b>logLevel</b>
	 * <p><b>Note:</b> This log level has no relationship
	 * with other Log framework(e.g. log4j, log-back, etc.),
	 * please do not mix up. :) 
	 */
	public Map<String,FileLoadStatus> load(LogLevel logLevel);
	
	/**
	 * The most comprehensive parameter-set method.
	 * @param logLevel {@link AnoleLoader#load(LogLevel)} 
	 * @param configLocations {@link AnoleLoader#load(String...)}
	 */
	public Map<String,FileLoadStatus> load(LogLevel logLevel, String ... configLocations);
}
