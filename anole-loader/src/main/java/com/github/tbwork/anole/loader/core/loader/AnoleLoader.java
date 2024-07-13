package com.github.tbwork.anole.loader.core.loader;


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
