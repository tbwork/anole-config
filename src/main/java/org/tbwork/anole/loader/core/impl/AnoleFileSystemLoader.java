package org.tbwork.anole.loader.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.loader.core.ConfigManager;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.exceptions.ConfigFileNotExistException;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.util.StringUtil;

import com.google.common.collect.Lists;

import lombok.Data;

import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;
import org.tbwork.anole.loader.util.FileUtil;
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
		// get resource stream in jar
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
	 
	
	private static class AnoleFilePath {
		private List<String> pathPartList;
		
		public AnoleFilePath(String fullPath){
			pathPartList = Lists.newArrayList(fullPath.split("\\\\|/"));
		}
		
		public boolean  isFuzzyDirectory(){
			int i = 0;
			for(i = 0; i < pathPartList.size(); i++){
				if(pathPartList.get(i).contains("*")){
					break;
				}
			}
			return i < pathPartList.size()-1;
		}
		
		public String getSolidDirectory(){
			int i = 0;
			for(i = 0; i < pathPartList.size(); i++){
				if(pathPartList.get(i).contains("*")){
					break;
				}
			}
			return StringUtil.join("/", pathPartList.subList(0, Math.min(i, pathPartList.size()-1))) + "/";
		}
	}

	
	private void loadFileFromDirectory(String fileFullPath){ 
		if(!fileFullPath.contains("*")){
			acfParser.parse(newInputStream(fileFullPath), fileFullPath);
		}
		else
		{ 
			AnoleFilePath afp = new AnoleFilePath(fileFullPath);
			if(afp.isFuzzyDirectory()){
				AnoleLogger.warn("Use asterisk in directory is not recomended, e.g., D://a/*/*.txt. We hope you know that it will cause plenty cost of time to seek every matched file.");
			}
			String solidDirectory = afp.getSolidDirectory();
			File directory = new File(solidDirectory);
			if(!directory.exists())
			   throw new ConfigFileDirectoryNotExistException(fileFullPath);
			List<File> files = FileUtil.getFilesInDirectory(solidDirectory);
			for(File file : files){
				if(StringUtil.asteriskMatch(fileFullPath, file.getAbsolutePath())){
					 acfParser.parse(newInputStream(fileFullPath), fileFullPath);
				}
			}
		}
	}
	
	
	
	// input like : D://prject/a.jar!/BOOT-INF!/classes!/*.properties
	private void loadFileFromJar(String fileFullPath){
		String jarPath = ProjectUtil.getJarPath(fileFullPath)+"/"; 
	    String directRelativePath = fileFullPath.replace("!", "").replace(jarPath, "");
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
