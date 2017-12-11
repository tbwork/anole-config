package org.tbwork.anole.loader.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.loader.core.ConfigManager;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.exceptions.ConfigFileNotExistException;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.util.StringUtil;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;
import org.tbwork.anole.loader.util.SingletonFactory; 

public class AnoleFileSystemLoader implements AnoleLoader{ 
	
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance();
	
	private ConfigManager cm;
	 
	public AnoleFileSystemLoader(){
		cm = SingletonFactory.getLocalConfigManager();
	}
	
	public AnoleFileSystemLoader(ConfigManager cm){
		this.cm = cm ;
	}
	

	@Override
	public void load() {
		load(AnoleLogger.defaultLogLevel);
	}  
 
	@Override
	public void load(LogLevel logLevel) { 
		AnoleLogger.anoleLogLevel = logLevel; 
	    throw new OperationNotSupportedException();
	}

	@Override
	public void load(String... configLocations) {
		load(AnoleLogger.defaultLogLevel, configLocations);
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
	  
	private InputStream newInputStream(String filepath){ 
		File file = new File(filepath);
		if(file.exists()){
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// never goes here
			}
		}
		
		InputStream fileInJarStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filepath);
		if(fileInJarStream!=null)
			return fileInJarStream;
		
		throw new ConfigFileNotExistException(filepath);
	}
	
	protected void loadFile(String fileFullPath){ 
		AnoleLogger.debug("Loading config files matchs '{}'", fileFullPath);
		if(fileFullPath.contains("!/")){ // For Spring Boot projects
			loadFileFromJar(fileFullPath);
		}
		else{
			loadFileFromDirectory(fileFullPath);
		}
	} 
	
	private void loadFileFromDirectory(String fileFullPath){
		String cl = fileFullPath.replaceAll("/+", "/"); 
		int dirTailIndex = cl.length()-1;
		for(; dirTailIndex>=0; dirTailIndex--)
		   if(cl.charAt(dirTailIndex)=='/')
			   break; 
		String filename = cl.substring(dirTailIndex+1);
		
		if(!filename.contains("*")){
			acfParser.parse(newInputStream(fileFullPath), filename);
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
				   acfParser.parse(newInputStream(dirPath+tfname), dirPath+tfname);
			   }
			}
		}
	}
	
	// input like : D://prject/a.jar!/BOOT-INF!/classes!/*.properties
	private void loadFileFromJar(String fileFullPath){
	    String jarPath = ProjectUtil.getJarPath(fileFullPath); 
	    String directRelativePath = fileFullPath.replace(jarPath, "").replace("!/", "");
	    JarFile file;
		try {
			file = new JarFile(jarPath);
			Enumeration<JarEntry> entrys = file.entries();
			while(entrys.hasMoreElements()){
		        JarEntry fileInJar = entrys.nextElement();
		        String fileInJarName = fileInJar.getName();
		        if(StringUtil.asteriskMatch(directRelativePath, fileInJarName)){
		        	AnoleLogger.debug("New config file ({}) was found. Parsing...", fileInJarName);
					acfParser.parse(file.getInputStream(fileInJar), fileInJarName);
				}
			}   
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
