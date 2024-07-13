package com.github.tbwork.anole.loader.context.impl;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.loader.core.loader.impl.AnoleFileLoader;


/**
 * <p>Before using Anole to manage your configuration, 
 * you should create the configuration context. It is
 * recommended to use the {@link AnoleConfigLocation} to
 * setup your application. However in some case you may 
 * need to create the configuration context manually.
 * And this is a way to load full-path property files.
 * <p>Usage example:
 *    	
 * <pre>
 *    AnoleFileConfigContext acc = new AnoleFileConfigContext(configLocations, jarPatterns);
 *    //use Anole as you like.
 * </pre>
 * <p> <b>About LogLevel:</b> The anole does not use any log implement
 * in the startup stage, it only providers the standard output to the
 * console window. When the application started, it use SLF4J facade to 
 * print logs. 
 * @author tbwork
 * @see AnoleClasspathConfigContext
 */
public class AnoleFileConfigContext extends AbstractAnoleContext{

	public AnoleFileConfigContext(String [] configLocations) {
		super(configLocations);
		create();
	}


	@Override
	protected String[] getDefaultConfigLocations() {
		throw new UnsupportedOperationException("This operation is not supported in the current mode. You must " +
				"specified a concrete configuration path.");
	}

	@Override
	protected void create() {
		new AnoleFileLoader(environment).load(getConfigLocations());
	}


}
