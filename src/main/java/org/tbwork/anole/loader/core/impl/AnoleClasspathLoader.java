package org.tbwork.anole.loader.core.impl;
 
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.loader.core.ConfigManager;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.StringUtil;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel; 

public class AnoleClasspathLoader extends AnoleFileSystemLoader{ 
	
	private AnoleLogger logger;
			
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance(); 
	  
	public AnoleClasspathLoader(){
		super();
	}
	
	public AnoleClasspathLoader(ConfigManager cm){
		super(cm);
	}
	
	@Override
	public void load(LogLevel logLevel) { 
		AnoleLogger.anoleLogLevel = logLevel; 
		load(logLevel, "*.anole"); 
	}
	
	@Override
	public void load(LogLevel logLevel, String... configLocations) {
		 configLocations = StringUtil.prefixString(configLocations, ProjectUtil.classPath);
		 super.load(logLevel, configLocations);
		 Anole.initialized = true;
	}
  
}
