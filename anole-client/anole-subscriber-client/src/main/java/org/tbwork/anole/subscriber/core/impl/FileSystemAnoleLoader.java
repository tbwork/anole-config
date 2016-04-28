package org.tbwork.anole.subscriber.core.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig;
import org.tbwork.anole.subscriber.core.AnoleLoader;
import org.tbwork.anole.subscriber.exceptions.ConfigFileNotExistException;
import org.tbwork.anole.subscriber.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.subscriber.exceptions.EnvironmentNotSetException;
import org.tbwork.anole.subscriber.exceptions.ErrorSyntaxException;
import org.tbwork.anole.subscriber.exceptions.OperationNotSupportedException;
import org.tbwork.anole.subscriber.util.RegexUtil;

public class FileSystemAnoleLoader implements AnoleLoader{

	private static final Logger logger = LoggerFactory.getLogger(FileSystemAnoleLoader.class);
	
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance();
	
	private AnoleSubscriberClient client = AnoleSubscriberClient.instance();
	
	@Override
	public void load(String configLocation) {
		 load(new String[] {configLocation});
	}

	@Override
	public void load(String... configLocations) {
		 for(String ifile : configLocations) 
			 loadFile(ifile, logger); 
		 AnoleConfig.initialized = true; 
		 logger.info("[:)] Anole configurations is loaded succesfully.");
		 client.connect();
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
				   if(RegexUtil.asteriskMatch(filename,tfname))
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
