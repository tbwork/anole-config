package org.tbwork.anole.loader.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.ConfigManager;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.StringUtil; 

public class AnoleClasspathLoader extends AnoleFileSystemLoader{ 
	
	private static final Logger logger = LoggerFactory.getLogger(AnoleClasspathLoader.class);
			
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance(); 
	  
	public AnoleClasspathLoader(){
		super();
	}
	
	public AnoleClasspathLoader(ConfigManager cm){
		super(cm);
	}
	
	@Override
	public void load() {
		 load("*.anole"); 
	} 
	
	@Override
	public void load(String... configLocations) {
		 configLocations = StringUtil.prefixString(configLocations, ProjectUtil.classPath);
		 super.load(configLocations);
		 AnoleLocalConfig.initialized = true;
	}
  
}
