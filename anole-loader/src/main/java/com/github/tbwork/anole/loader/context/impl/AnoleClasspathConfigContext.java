package com.github.tbwork.anole.loader.context.impl;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.loader.core.loader.impl.AnoleClasspathLoader;

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
public class AnoleClasspathConfigContext extends AbstractAnoleContext{

	private String [] includeClasspathPatterns;
	private String [] excludeClasspathPatterns;

	public AnoleClasspathConfigContext(String [] configLocations, String [] includeClassPathDirectoryPatterns,
									   String [] excludeClassPathDirectoryPatterns){
		super(configLocations);

		if(includeClassPathDirectoryPatterns.length != 0){
			includeClasspathPatterns =  includeClassPathDirectoryPatterns;
		}
		else{
			includeClasspathPatterns = new String[0] ;
		}

		if( excludeClassPathDirectoryPatterns.length != 0){
			excludeClasspathPatterns = excludeClassPathDirectoryPatterns;
		}
		else{
			excludeClasspathPatterns = new String[0] ;
		}

		create();
	}


	public AnoleClasspathConfigContext(){
		super(null);
		includeClasspathPatterns = new String[0] ;
		excludeClasspathPatterns = new String[0] ;
		create();
	}


	@Override
	protected String[] getDefaultConfigLocations() {
		return new String[]{"*.anole"};
	}

	@Override
	protected void create() {
		new AnoleClasspathLoader(environment, includeClasspathPatterns, excludeClasspathPatterns).load(getConfigLocations());
	}
}
