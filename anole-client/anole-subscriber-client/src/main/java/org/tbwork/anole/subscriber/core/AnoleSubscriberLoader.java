package org.tbwork.anole.subscriber.core;
 

/**
 * <p>Before use Anole subscriber, you should initialize
 *  it by loading the configuration file manually, or 
 * setup it in a web application's web.xml.
 * <p>Usage example:
 *    	
 * <pre>
 *    AnoleSubscriberLoader anoleLoader = new AnoleSubscriberClasspathLoader();
 *    anoleLoader.load(
 *       file1,
 *       file2
 *     );
 *    //or anoleLoader.load();
 *    //or anoleLoader.load(file1);
 *    //then use AnoleConfig as you like.
 * </pre>
 * <p> <b>Tips:</b> Although Anole allows you to set configuration
 * locally, but we strongly recommend you put most of your 
 * configurations to the Anole server except those necessary ones.
 * @author Tommy.Tang
 * @see AnoleSubscriberLoader#load()
 * @see AnoleSubscriberLoader#load(String...)
 */
public interface AnoleSubscriberLoader { 
	
	/**
	 * Load "*.anole" files within the class-path directories.
	 */
	public void load(); 
	/**
	 * Load multiple configuration files. 
	 */
	public void load(String ... configLocations);
 
	
}
