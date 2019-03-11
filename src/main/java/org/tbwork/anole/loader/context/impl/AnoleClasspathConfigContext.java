package org.tbwork.anole.loader.context.impl;

import org.tbwork.anole.loader.annotion.AnoleClassPathFilter;
import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleCallBack;
import org.tbwork.anole.loader.core.loader.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.util.PathUtil;
import org.tbwork.anole.loader.util.SetUtil;

import java.util.Map;

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
public class AnoleClasspathConfigContext{

	private Map<String, Boolean> alreadyFoundOrMatchedMap;

	public AnoleClasspathConfigContext(AnoleCallBack anoleCallBack, AnoleClassPathFilter classPathFilter, String ... configLocations) {
		AnoleLoader anoleLoader = new AnoleClasspathLoader(classPathFilter);
		anoleLoader.setCallback(anoleCallBack);
		String [] slashProcessedPathes = PathUtil.format2SlashPathes(configLocations);
		MatchCounter.initialize(SetUtil.newArrayList(configLocations));
		anoleLoader.load(slashProcessedPathes);
		MatchCounter.checkNotExist();
	}


	public AnoleClasspathConfigContext(AnoleClassPathFilter classPathFilter, String ... configLocations) {
		this(null, classPathFilter, configLocations);
	}

	public AnoleClasspathConfigContext(AnoleCallBack anoleCallBack, String ... configLocations) {
		this(anoleCallBack, null, configLocations);
	}

	public AnoleClasspathConfigContext(AnoleCallBack anoleCallBack) {
		this(anoleCallBack, (AnoleClassPathFilter) null, "*.anole");
	}

	public AnoleClasspathConfigContext(String ... configLocations) {
		this(null, null, configLocations);
	}

	public AnoleClasspathConfigContext() {
		this("*.anole");
	}






}
