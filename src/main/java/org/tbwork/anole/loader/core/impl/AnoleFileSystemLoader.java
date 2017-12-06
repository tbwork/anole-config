package org.tbwork.anole.loader.core.impl;

import java.io.File;
  
import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.loader.core.ConfigManager;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.exceptions.ConfigFileNotExistException;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.util.StringUtil;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;
import org.tbwork.anole.loader.util.SingletonFactory; 

public class AnoleFileSystemLoader implements AnoleLoader{ 
	
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance();
	
	private ConfigManager cm;
	
	private LogLevel defaultLogLevel = LogLevel.INFO;
	
	public AnoleFileSystemLoader(){
		cm = SingletonFactory.getLocalConfigManager();
	}
	
	public AnoleFileSystemLoader(ConfigManager cm){
		this.cm = cm ;
	}
	

	@Override
	public void load() {
		load(defaultLogLevel);
	}  
 
	@Override
	public void load(LogLevel logLevel) { 
		AnoleLogger.anoleLogLevel = logLevel; 
	    throw new OperationNotSupportedException();
	}

	@Override
	public void load(String... configLocations) {
		load(defaultLogLevel, configLocations);
	} 
	
	@Override
	public void load(LogLevel logLevel, String... configLocations) {
		AnoleLogger.anoleLogLevel = logLevel; 
		for(String ifile : configLocations) 
			 loadFile(ifile); 
		Anole.initialized = true; 
		cm.postProcess();
		AnoleLogger.info("[:)] Anole configurations are loaded succesfully.");
	}
	  
	private File newFile(String filepath){ 
		File file = new File(filepath);
		if(file.exists())
			return file;
		else
			throw new ConfigFileNotExistException(filepath);
	}
	
	protected void loadFile(String fileFullPath){ 
		String cl = fileFullPath.replaceAll("/+", "/"); 
		int dirTailIndex = cl.length()-1;
		for(; dirTailIndex>=0; dirTailIndex--)
		   if(cl.charAt(dirTailIndex)=='/')
			   break; 
		String filename = cl.substring(dirTailIndex+1);
		
		if(!filename.contains("*"))
		{
			acfParser.parse(newFile(fileFullPath));
		}
		else
		{ 
			String dirPath = cl.substring(0, dirTailIndex+1);
			if(dirPath.isEmpty())
			   throw new ConfigFileNotExistException(fileFullPath);
			File file=new File(dirPath);
			if(!file.exists())
			   throw new ConfigFileDirectoryNotExistException(dirPath);
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
			   String tfname = fileList[i].getName();
			   if(StringUtil.asteriskMatch(filename,tfname))
			   {
				   acfParser.parse(newFile(dirPath+tfname));
			   }
			}
		}
	}
}
