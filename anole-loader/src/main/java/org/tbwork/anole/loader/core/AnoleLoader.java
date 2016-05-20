package org.tbwork.anole.loader.core;
 

/**
 * <p>Before use Anole, you should initialize it
 * by loading the configuration file manually, or 
 * setup it in a web application's web.xml.
 * <p>Usage example:
 *    	
 * <pre>
 *    AnoleLoader anoleLoader = new ClasspathAnoleLoader();
 *    anoleLoader.load(
 *       file1,
 *       file2
 *     );
 *    //or anoleLoader.load();
 *    //or anoleLoader.load(file1);
 *    //use AnoleConfig as you like.
 * </pre>
 * <p> <b>Tips:</b> Although Anole allows you to set configuration
 * locally, but we strongly recommend you put most of your 
 * configurations to the anole server.
 * @author Tommy.Tang
 * @see AnoleLoader#load()
 * @see AnoleLoader#load(String...)
 */
public interface AnoleLoader { 
	
	/**
	 * Load "*.anole" files within the class-path directories.
	 */
	public void load(); 
	/**
	 * Load multiple configuration files. 
	 */
	public void load(String ... configLocations);
 
	
}
