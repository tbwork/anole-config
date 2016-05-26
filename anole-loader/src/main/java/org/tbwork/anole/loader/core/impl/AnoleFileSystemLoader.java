package org.tbwork.anole.loader.core.impl;

import java.io.File;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.exceptions.ConfigFileNotExistException;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.util.StringUtil;
import org.tbwork.anole.loader.util.SingletonFactory; 

public class AnoleFileSystemLoader implements AnoleLoader{

	private static final Logger logger = LoggerFactory.getLogger(AnoleFileSystemLoader.class);
	
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance();
	
	private final LocalConfigManager lcm = SingletonFactory.getLocalConfigManager();
	
	@Override
	public void load(String... configLocations) {
		 for(String ifile : configLocations) 
			 loadFile(ifile, logger); 
		 AnoleLocalConfig.initialized = true; 
		 logger.info("[:)] Local anole configurations are loaded succesfully.");
	} 

	@Override
	public void load() {
		 throw new OperationNotSupportedException();
	} 
	
	protected void loadFile(String fileFullPath, Logger logger){ 
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
	
	private File newFile(String filepath){ 
		File file = new File(filepath);
		if(file.exists())
			return file;
		else
			throw new ConfigFileNotExistException(filepath);
	}
	 
}
