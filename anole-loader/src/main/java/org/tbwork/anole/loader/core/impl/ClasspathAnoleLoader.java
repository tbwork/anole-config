package org.tbwork.anole.loader.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.util.ProjectUtil; 

public class ClasspathAnoleLoader extends FileSystemAnoleLoader{

	private static final Logger logger = LoggerFactory.getLogger(ClasspathAnoleLoader.class);
			
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance(); 
	  
	@Override
	public void load() {
		 load("*.anole"); 
	} 
	
	@Override
	public void load(String... configLocations) {
		 for(String ifile : configLocations) 
			 loadFile(ProjectUtil.classPath+ifile, logger); 
		 AnoleLocalConfig.initialized = true;
	}
  
}
