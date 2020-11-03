package org.tbwork.anole.loader.core.parser;

import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.exceptions.EnvironmentNotSetException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.util.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
 

public class AnoleConfigFileParser {

	private static final AnoleLogger logger = new AnoleLogger(AnoleConfigFileParser.class) ;

	private static final AnoleConfigFileParser anoleConfigFileParser = new AnoleConfigFileParser();


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
	
	public static AnoleConfigFileParser getInstance(String environment){
		anoleConfigFileParser.sysEnv = environment;
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
			String lineText = s.nextLine().trim();
			RawKV rawKV = parseLine(lineText, s);
			if(rawKV != null){
				result.add(rawKV);
			}
		}
		return result;
	}
	
	private RawKV parseLine(String content, Scanner scanner){

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
		if(!sysEnv.equals(currentEnvironment) && !"all".equals(currentEnvironment)){
			// skip other environments
			return null;
		}


		while( content.endsWith("\\") && scanner.hasNextLine()){
			content = content.substring(0, content.length()-1);
			content =  content +  scanner.nextLine().trim();
		}

		return new RawKV(separateKV(content));
	}



	private String getCurrentEnvironment(){
		// check by the following order
		// 1. the jvm system property
		// 2. the os system property
		// 3. the environment file
		//check if the environment is already set or not
		sysEnv = System.getProperty("anole.env");
		if(S.isEmpty(sysEnv)){
			sysEnv = System.getProperty("anole.environment");
		}
		if(S.isEmpty(sysEnv)){
			sysEnv = System.getenv("ANOLE_ENV");
		}
		if(S.isEmpty(sysEnv)){
			sysEnv = System.getenv("ANOLE_ENVIRONMENT");
		}

		if(S.isEmpty(sysEnv)){
			sysEnv = getEnvFromFile();
		}

		if(S.isNotEmpty(sysEnv)) {
			return sysEnv;
		}

		//throw new EnvironmentNotSetException();
		// from 1.2.5 use warning instead and return "all" environment.
		logger.info("Cound not decide current environment, 'all' environment will be used.");
		sysEnv = "all";
		return sysEnv;

	}


	private String getEnvFromFile(){
		switch(OsUtil.getOsCategory()){
			case WINDOWS:{
				return getEnvFromFile("C://anole/");
			}
			case LINUX:{
				return getEnvFromFile("/etc/anole/");
			}
			case MAC:{
				return getEnvFromFile("/Users/anole/");
			}
			default: return null;
		}
	}

	private String getEnvFromFile(String directoryPath){
		File file = new File(directoryPath);
		if(file.exists()){
			File [] fileList = file.listFiles();
			for(File ifile : fileList){
				String ifname = ifile.getName();
				if(S.asteriskMatch("*.env", ifname)){
					sysEnv = ifname.replace(".env", "");
					return sysEnv;
				}
			}
		}
		return null;
	}


	private String [] separateKV(String kvString){
		int index = kvString.indexOf('='); 
		if(index < 0 )
			throw new ErrorSyntaxException(lineNumber, currentFileName, "Could not find the '=' symbol.");
		else
			return SetUtil.newArrayList(kvString.substring(0, index), kvString.substring(index+1)).toArray(new String[2]);
	}
}
