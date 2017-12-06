package org.tbwork.anole.loader.core.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner; 
import org.tbwork.anole.loader.types.ConfigType; 
import org.tbwork.anole.loader.exceptions.ConfigFileNotExistException;
import org.tbwork.anole.loader.exceptions.EnvironmentNotSetException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.OsUtil;
import org.tbwork.anole.loader.util.StringUtil;

import com.google.common.collect.Lists;

import org.tbwork.anole.loader.util.SingletonFactory;
 

public class AnoleConfigFileParser {

	private static final AnoleConfigFileParser anoleConfigFileParser = new AnoleConfigFileParser();
	
	private AnoleLogger logger ;
	private final LocalConfigManager lcm = SingletonFactory.getLocalConfigManager();
	
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
		if(sysEnv == null || sysEnv.isEmpty())
			throw new EnvironmentNotSetException();
		lineNumber = 0;
		configEnv = "all";//default environment is all
		currentFileFullPath = file.getAbsolutePath();
		try{
			Scanner s = new Scanner(file);  
			while(s.hasNextLine()){
				lineNumber++;
				parseLine(StringUtil.trim(s.nextLine()));
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
		else if(content.trim().startsWith("#env:")) {
			configEnv = content.trim().replace("#env:", "").trim();
		}else if(content.charAt(0) == '#')
			return;
		else
			parseKV(content);
	}
	
	private void parseKV(String content){
		//t:type k:key v:value
		String [] tkvArray = separateKV(content); 
		String tk = tkvArray[0].trim();
		String v = tkvArray[1].trim();
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
					if(v == null)
						v = "";
					lcm.setConfigItem(k, v, ConfigType.STRING);
				}break;
				case "b":{ 
					lcm.setConfigItem(k, v, ConfigType.BOOL);
				}break;
				case "n":{ 
					lcm.setConfigItem(k, v, ConfigType.NUMBER);
				}break;
				default:{
					throw new ErrorSyntaxException(lineNumber, currentFileFullPath, "Unknow value type : " + t);
				} 
			} 
		}   
		else if(logger.isDebugEnabled())
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
		// check the env file first
		if(!file.exists()){
			//check if the environment is already set or not
			sysEnv = System.getProperty("anole.runtime.currentEnvironment");
			if(sysEnv != null)
				return ;
			throw new EnvironmentNotSetException();
		}  
		File [] fileList = file.listFiles();
		for(File ifile : fileList){
			String ifname = ifile.getName();
			if(StringUtil.asteriskMatch("*.env", ifname)){
				sysEnv = ifname.replace(".env", "");
				lcm.setConfigItem("anole.runtime.currentEnvironment", sysEnv, ConfigType.STRING);
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
