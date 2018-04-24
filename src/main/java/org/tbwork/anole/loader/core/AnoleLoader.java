package org.tbwork.anole.loader.core;

import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

/**
 * <p>Before use Anole, you should initialize it
 * by loading the configuration file manually, or 
 * setup it in a web application's web.xml.
 * <p>Usage example:
 *    	
 * <pre>
 *    AnoleLoader anoleLoader = new AnoleClasspathLoader();
 *    anoleLoader.load(
 *       file1,
 *       file2
 *     );
 *    //or anoleLoader.load();
 *    //or anoleLoader.load(file1);
 *    //use AnoleLocalConfig as you like.
 * </pre>
 * <p> <b>Tips:</b> In order to let the other framework (like Spring,
 * Log4j, Log4j2, etc.) to load properties via Anole mechanism.
 * <p> <b>About LogLevel:</b> The anole does not use any log implement
 * in the startup stage, it only providers the standard output on the
 * console window. After startup, it would use SLF4J facade to print logs
 * after when the logging framework (e.g., log4j,log4j2,log-back) you 
 * configured will be used.
 * @author Tommy.Tang
 * @see AnoleLoader#load()
 * @see AnoleLoader#load(String...)
 * @see AnoleLoader#load(LogLevel)
 * @see AnoleLoader#load(LogLevel, String...)
 */
public interface AnoleLoader {
	/**
	 * Load "*.anole" files within the class-path directories.
	 * <p><b>Note:</b> Default log level is INFO.
	 */
	public void load(); 
	/**
	 * Load multiple configuration files. 
	 */
	public void load(String ... configLocations);
 
	/**
	 * <p>You can control the log level of Anole's output in
	 * the startup stage by setting <b>logLevel</b>
	 * <p><b>Note:</b> This log level has no relationship
	 * with other Log framework(e.g. log4j, log-back, etc.),
	 * please do not mix up. :) 
	 */
	public void load(LogLevel logLevel);
	
	/**
	 * The most comprehensive parameter-set method.
	 * @param logLevel {@link AnoleLoader#load(LogLevel)} 
	 * @param configLocations {@link AnoleLoader#load(String...)}
	 */
	public void load(LogLevel logLevel, String ... configLocations);
}
