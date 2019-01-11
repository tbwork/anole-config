package org.tbwork.anole.loader.core.loader.impl;

import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.core.manager.impl.LocalConfigManager;
import org.tbwork.anole.loader.exceptions.EnvironmentNotSetException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.loader.util.*;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;
 

class AnoleConfigFileParser {

	private static final AnoleConfigFileParser anoleConfigFileParser = new AnoleConfigFileParser();
	
	private static AnoleLogger logger ;
	private static final LocalConfigManager lcm = SingletonFactory.getLocalConfigManager();
	
	private AnoleConfigFileParser(){
		String env = setEnv(); 
		AnoleApp.setEnvironment(env);
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
	
	private String currentFileName;
	
	public static AnoleConfigFileParser instance(){
		return anoleConfigFileParser;
	}
	
	public void parse(InputStream is, String fileName) { 
		if(sysEnv == null || sysEnv.isEmpty())
			throw new EnvironmentNotSetException();
		lineNumber = 0;
		configEnv = "all";//default environment is all 
		Scanner s = new Scanner(is);  
		currentFileName = fileName;
		while(s.hasNextLine()){
			lineNumber++;
			parseLine(StringUtil.trim(s.nextLine()));
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
				throw new ErrorSyntaxException(lineNumber, currentFileName, "To many ':' symbols in the line.");
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
					throw new ErrorSyntaxException(lineNumber, currentFileName, "Unknow value type : " + t);
				} 
			} 
		}				
	}
	
	private String setEnv(){
		switch(OsUtil.getOsCategory()){
			case WINDOWS:{
				return setEnvFromPath("C://anole/");
			}
			case LINUX:{
				return setEnvFromPath("/etc/anole/");
			}
			case MAC:{
				return setEnvFromPath("/Users/anole/");
			}
			default: return null;
		}
	}
	
	private String setEnvFromPath(String directoryPath){ 
		// check by the following order
		// 1. the system property
		// 2. the JVM boot variable
		// 3. the environment file
		//check if the environment is already set or not
		sysEnv = System.getProperty("anole.runtime.currentEnvironment");
		if(sysEnv == null)
			sysEnv = System.getenv("anole.runtime.currentEnvironment"); 
		
		if(sysEnv != null && !sysEnv.isEmpty()) {
			lcm.setConfigItem("anole.runtime.currentEnvironment", sysEnv, ConfigType.STRING);
			return sysEnv; 
		} 
		
		File file = new File(directoryPath);
		if(file.exists()){
			File [] fileList = file.listFiles();
			for(File ifile : fileList){
				String ifname = ifile.getName();
				if(StringUtil.asteriskMatch("*.env", ifname)){
					sysEnv = ifname.replace(".env", ""); 
					return sysEnv;
				}
			}
		}  
		//throw new EnvironmentNotSetException();
		// from 1.2.5 use warning instead and return "all" environment.
		AnoleLogger.info("Cound not decide current environment, 'all' environment will be used.");
		sysEnv = "all";
		return sysEnv;
		
	}
	
	private String [] separateKV(String kvString){
		int index = kvString.indexOf('='); 
		if(index < 0 )
			throw new ErrorSyntaxException(lineNumber, currentFileName, "Could not find the '=' symbol.");
		else
			return SetUtil.newArrayList(kvString.substring(0, index), kvString.substring(index+1)).toArray(new String[2]);
	}
}
