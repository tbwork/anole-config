package org.tbwork.anole.loader.core.parser;

import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.core.manager.impl.LocalConfigManager;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.exceptions.EnvironmentNotSetException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.loader.util.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
 

public class AnoleConfigFileParser {

	private static final AnoleConfigFileParser anoleConfigFileParser = new AnoleConfigFileParser();

	private static final LocalConfigManager lcm = SingletonFactory.getLocalConfigManager();

	private static AnoleLogger logger ;

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
	private String currentEnvironment;
	
	private String currentFileName;
	
	public static AnoleConfigFileParser instance(){
		return anoleConfigFileParser;
	}
	
	public List<RawKV> parse(InputStream is, String fileName) {
		if(sysEnv == null || sysEnv.isEmpty())
			throw new EnvironmentNotSetException();
		List<RawKV> result = new ArrayList<>();
		lineNumber = 0;
		currentEnvironment = "all";//default environment is all
		Scanner s = new Scanner(is);  
		currentFileName = fileName;
		while(s.hasNextLine()){
			lineNumber++;
			result.add(parseLine(StringUtil.trim(s.nextLine())));
		}
		return result;
	}
	
	private RawKV parseLine(String content){
		if(content == null || content.isEmpty()){
			return null;
		}
		if(content.trim().startsWith("#env:")) {
			currentEnvironment = content.trim().replace("#env:", "").trim();
			return null;
		}
		if(content.charAt(0) == '#'){
			// skip comments
			return null;
		}
		if(!sysEnv.equals(currentEnvironment) && !"all".equals(sysEnv)){
			// skip other environments
			return null;
		}

		return new RawKV(separateKV(content));
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
