package org.tbwork.anole.loader.core.loader;

import org.tbwork.anole.loader.core.loader.impl.AnoleCallBack;

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

	/**
	 * Set a callback logic which will be called after all configuration files were loaded.
	 * @param anoleCallBack
	 */
	public void setCallback(AnoleCallBack anoleCallBack);
 
}
