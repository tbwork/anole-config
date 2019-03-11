package org.tbwork.anole.loader.core.loader.impl;

import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.core.manager.impl.LocalConfigManager;
import org.tbwork.anole.loader.exceptions.EnvironmentNotSetException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.SetUtil;
import org.tbwork.anole.loader.util.SingletonFactory;
import org.tbwork.anole.loader.util.StringUtil;

import java.io.InputStream;
import java.util.Scanner;
 

class AnoleConfigFileParser {

	private static final AnoleConfigFileParser anoleConfigFileParser = new AnoleConfigFileParser();
	
	private static AnoleLogger logger ;
	private static final LocalConfigManager lcm = SingletonFactory.getLocalConfigManager();

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
		if(StringUtil.isNullOrEmpty(AnoleApp.getEnvironment()))
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
		content = content.replace("\uFEFF", "");
		String envContent = StringUtil.removeBlankChars(content);
		if(envContent.trim().startsWith("#env:") || envContent.trim().startsWith("#environment:")) {
			configEnv = content.split(":")[1];
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
		String sysEnv = AnoleApp.getEnvironment();
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
	

	
	private String [] separateKV(String kvString){
		int index = kvString.indexOf('='); 
		if(index < 0 )
			throw new ErrorSyntaxException(lineNumber, currentFileName, "Could not find the '=' symbol.");
		else
			return SetUtil.newArrayList(kvString.substring(0, index), kvString.substring(index+1)).toArray(new String[2]);
	}
}
