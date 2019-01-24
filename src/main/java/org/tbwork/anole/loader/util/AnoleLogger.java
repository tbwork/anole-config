package org.tbwork.anole.loader.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.context.Anole;

import java.util.regex.Matcher;

public class AnoleLogger {

	public static volatile LogLevel anoleLogLevel = LogLevel.INFO; // default is INFO
	
	private static Logger logger = null;
	
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
		return anoleLogLevel.code() == LogLevel.DEBUG.code();
	}
	
	private static void coreLog(LogLevel logLevel, String baseInfo, Object ... variables){ 
		if(logLevel.code() >= anoleLogLevel.code()  ){
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
	
	/**
	 * E.g. <b>AnoleLogger.debug("{}-{}","a","b")</b> will output "a-b"
	 */
	public static void debug(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.DEBUG, "[DEBUG] "+baseInfo, variables);
		}
		else{ 
			getLogger().debug(baseInfo, variables);
		}
	}
	
	/**
	 * E.g. <b>AnoleLogger.info("{}-{}","a","b")</b> will output "a-b"
	 */
	public static void info(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.INFO, "[INFO] "+baseInfo, variables);
		}
		else{ 
			getLogger().info(baseInfo, variables);
		}
	}

	/**
	 * E.g. <b>AnoleLogger.warn("{}-{}","a","b")</b> will output "a-b"
	 */
	public static void warn(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.WARN, "[WARN] "+baseInfo, variables);
		}
		else{ 
			getLogger().warn(baseInfo, variables);
		}
	}

	/**
	 * E.g. <b>AnoleLogger.error("{}-{}","a","b")</b> will output "a-b"
	 */
	public static void error(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.ERROR, "[ERROR] "+baseInfo, variables);
		}
		else{ 
			getLogger().error(baseInfo, variables);
		}
	}
	
	/**
	 * E.g. <b>AnoleLogger.fatal("{}-{}","a","b")</b> will output "a-b"
	 */
	public static void fatal(String baseInfo, String ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.FATAL, "[FATAL] "+baseInfo, variables);
		}
		else{ 
			getLogger().error(baseInfo, variables);
		}
	}
	
	
	private static Logger getLogger(){
		if(logger == null){
			synchronized(AnoleLogger.class){
				if(logger == null)
					logger = LoggerFactory.getLogger(AnoleLogger.class);
			}
		}
		return logger; 
	}

	public static void flush(){
		if(!Anole.initialized){
			System.out.flush();
		}
	}
}
