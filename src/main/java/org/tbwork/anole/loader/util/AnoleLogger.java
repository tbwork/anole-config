package org.tbwork.anole.loader.util;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

public class AnoleLogger {

	public static volatile LogLevel anoleLogLevel;
	
	private static Logger logger = LoggerFactory.getLogger(AnoleLogger.class);;
	
	public static LogLevel defaultLogLevel = LogLevel.INFO;
	
	private static char placeHolderChar = 26 ; //ASCII code: SUB
	
	public static enum LogLevel{
		DEBUG(0),
		INFO(1),
		WARN(2),
		ERROR(3),
		FATAL(4),
		CLOSE(5);
		
		private int level;
		
		private LogLevel(int level){
			this.level = level;
		} 
		
		public int code(){
			return level;
		} 
	}
	
	public static boolean isDebugEnabled(){
		return anoleLogLevel.code() <= LogLevel.DEBUG.code();
	}
	
	private static void coreLog(LogLevel logLevel, String baseInfo, Object ... variables){ 
		if(anoleLogLevel.code() >= logLevel.code() ){
			String output = baseInfo.replace("{}", placeHolderChar+""); 
			for(Object variable : variables){
				if(variable == null)
					variable = "null";
				int index = output.indexOf(placeHolderChar);
				if(index == -1){
					break;
				}
				output = output.replaceFirst(placeHolderChar+"", Matcher.quoteReplacement(variable.toString()));
			}
			System.out.println(output);
		}
	}
	
	public static void debug(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.DEBUG, baseInfo, variables);
		}
		else{ 
			logger.debug(baseInfo, variables);
		}
	}
	
	public static void info(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.INFO, baseInfo, variables);
		}
		else{ 
			logger.info(baseInfo, variables);
		}
	}

	public static void warn(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.WARN, baseInfo, variables);
		}
		else{ 
			logger.warn(baseInfo, variables);
		}
	}

	public static void error(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.ERROR, baseInfo, variables);
		}
		else{ 
			logger.error(baseInfo, variables);
		}
	}
	
	public static void fatal(String baseInfo, String ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.FATAL, baseInfo, variables);
		}
		else{ 
			logger.error(baseInfo, variables);
		}
	}
	
}
