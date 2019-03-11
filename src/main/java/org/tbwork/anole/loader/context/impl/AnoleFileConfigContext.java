package org.tbwork.anole.loader.context.impl;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleCallBack;
import org.tbwork.anole.loader.core.loader.impl.AnoleFileLoader;
import org.tbwork.anole.loader.util.PathUtil;
import org.tbwork.anole.loader.util.SetUtil;

import java.util.Map;


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
 *    AnoleFileConfigContext acc = new AnoleFileConfigContext(LogLevel.INFO, configFilePath);
 *    //use Anole as you like.
 * </pre>
 * <p> <b>About LogLevel:</b> The anole does not use any log implement
 * in the startup stage, it only providers the standard output to the
 * console window. When the application started, it use SLF4J facade to 
 * print logs. 
 * @author tbwork
 * @see AnoleClasspathConfigContext
 */
public class AnoleFileConfigContext{
  
	private Map<String, Boolean> alreadyFoundOrMatchedMap;
	
	public AnoleFileConfigContext(String ... locationPatterns) {
		this(null, locationPatterns);
	}

	public AnoleFileConfigContext(AnoleCallBack anoleCallBack, String ... locationPatterns) {
		AnoleLoader anoleLoader = new AnoleFileLoader();
		anoleLoader.setCallback(anoleCallBack);
		String [] slashProcessedPathes = PathUtil.format2SlashPathes(locationPatterns);
		initializeMatchCounter(locationPatterns);
		anoleLoader.load(slashProcessedPathes);
		MatchCounter.checkNotExist();
	}

	private void initializeMatchCounter(String ... locationPatterns){
		MatchCounter.initialize(SetUtil.newArrayList(locationPatterns));
		for(String item : locationPatterns){
			MatchCounter.putConfigMap(PathUtil.uniformPath(item), item);
		}

	}
}
