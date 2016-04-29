package org.tbwork.anole.subscriber.core.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.subscriber.core.ConfigManager;
import org.tbwork.anole.subscriber.exceptions.ConfigFileNotExistException;
import org.tbwork.anole.subscriber.exceptions.EnvironmentNotSetException;
import org.tbwork.anole.subscriber.exceptions.ErrorSyntaxException;
import org.tbwork.anole.subscriber.util.OsUtil;
import org.tbwork.anole.subscriber.util.RegexUtil;

import com.google.common.collect.Lists;

public class AnoleConfigFileParser {

	private static final AnoleConfigFileParser anoleConfigFileParser = new AnoleConfigFileParser();
	
	private static Logger logger = LoggerFactory.getLogger(AnoleConfigFileParser.class);
	
	private AnoleConfigFileParser(){
		setEnv(); 
	}
	
	/**
	 * The environment type of current running os.
	 */
	private String sysEnv;
	
	private int lineNumber = 0;
	
	/**
	 * The environment type of current configuration
	 */
	private String configEnv;
	
	private String currentFileFullPath;
	
	public static AnoleConfigFileParser instance(){
		return anoleConfigFileParser;
	}
	
	public void parse(File file) {
		if(sysEnv== null || sysEnv.isEmpty())
			throw new EnvironmentNotSetException();
		lineNumber = 0;
		configEnv = "";
		currentFileFullPath = file.getAbsolutePath();
		try{
			Scanner s = new Scanner(file);  
			while(s.hasNextLine()){
				lineNumber++;
				parseLine(s.nextLine());
			}
		}
		catch(FileNotFoundException e)
		{
			throw new ConfigFileNotExistException(file.getPath());
		} 
	}
	
	private void parseLine(String content){
		if(content==null || content.isEmpty())
			return;
		else if(content.charAt(0) == '#')
			return;
		else
			parseKV(content);
	}
	
	private void parseKV(String content){
		//t:type k:key v:value
		String [] tkvArray = separateKV(content); 
		String tk = tkvArray[0].trim();
		String v = tkvArray[1].trim();
		if("env".equals(tk)) {
			configEnv = v;
		}
		if(sysEnv.equals(configEnv) || "all".equals(configEnv)){
			String [] tkArray = tk.split(":");  
			if(tkArray.length >2)
				throw new ErrorSyntaxException(lineNumber, currentFileFullPath, "To many ':' symbols in the line.");
			String t = "s";
			String k = tk; 
			if(tkArray.length == 2){
				t = tkArray[0].trim().toLowerCase(); 
				k = tkArray[1].trim();
			}
			switch(t){
				case "s":{
					ConfigManager.checkAndInitialConfig(k);// for dynamic load in the future.
					ConfigManager.setConfigItem(k, v, ConfigType.STRING);
				}break;
				case "b":{
					ConfigManager.checkAndInitialConfig(k);
					ConfigManager.setConfigItem(k, v, ConfigType.BOOL);
				}break;
				case "n":{
					ConfigManager.checkAndInitialConfig(k);
					ConfigManager.setConfigItem(k, v, ConfigType.NUMBER);
				}break;
				default:{
					throw new ErrorSyntaxException(lineNumber, currentFileFullPath, "Unknow value type : " + t);
				} 
			} 
		}   
		
		if(logger.isDebugEnabled())
			logger.debug("Configuration ignored! Current system environtment is {}, and env-scope of current configuration lines is {}", sysEnv, configEnv);
				
	}
	
	private void setEnv(){
		switch(OsUtil.getOsCategory()){
			case WINDOWS:{
				setEnvFromPath("C://anole/");
			} break;
			case LINUX:{
				setEnvFromPath("/etc/anole/");;
			}break;
			case MAC:{
				setEnvFromPath("/Users/anole/");
			}break;
			default: break;
		}
	}
	
	private void setEnvFromPath(String directoryPath){
		File file = new File(directoryPath);
		if(!file.exists())
		   throw new EnvironmentNotSetException();
		File [] fileList = file.listFiles();
		for(File ifile : fileList){
			String ifname = ifile.getName();
			if(RegexUtil.asteriskMatch("*.env", ifname)){
				sysEnv = ifname.replace(".env", "");
				return;
			}
		}
		if(sysEnv==null || sysEnv.isEmpty())
			throw new EnvironmentNotSetException();
	}
	
	private String [] separateKV(String kvString){
		int index = kvString.indexOf('='); 
		if(index < 0 )
			throw new ErrorSyntaxException(lineNumber, currentFileFullPath, "Could not find the '=' symbol.");
		else
			return Lists.newArrayList(kvString.substring(0, index), kvString.substring(index+1)).toArray(new String[2]);
	}
}
