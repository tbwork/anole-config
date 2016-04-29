package org.tbwork.anole.subscriber.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.subscriber.core.AnoleConfig;
import org.tbwork.anole.subscriber.core.AnoleLoader;
import org.tbwork.anole.subscriber.util.ProjectUtil;

public class ClasspathAnoleLoader extends FileSystemAnoleLoader{

	private static final Logger logger = LoggerFactory.getLogger(ClasspathAnoleLoader.class);
			
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance(); 
	  
	@Override
	public void load() {
		 load("*.anole"); 
	}
	
	@Override
	public void load(String configLocation) { 
		 load(new String[] {ProjectUtil.classPath+configLocation}); 
	}

	@Override
	public void load(String... configLocations) {
		 for(String ifile : configLocations) 
			 loadFile(ifile, logger); 
		 AnoleConfig.initialized = true;
	}
 
}
