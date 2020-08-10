package org.tbwork.anole.loader.util;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

public class AnoleLogger {

	public static volatile LogLevel anoleLogLevel = LogLevel.INFO; // default is INFO

	private static Logger logger = null;

	private Class owner;

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

	public AnoleLogger(Class<?> clazz){
		this.owner = clazz;
	}

	public boolean isDebugEnabled(){
		return anoleLogLevel.code() == LogLevel.DEBUG.code();
	}
	
	private void coreLog(LogLevel logLevel, String baseInfo, Object ... variables){
		if(logLevel.code() >= anoleLogLevel.code()  ){
			baseInfo = owner.getSimpleName() + " - " + baseInfo;
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
	public void debug(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.DEBUG, "[DEBUG] "+ baseInfo, variables);
		}
		else{ 
			getLogger().debug(baseInfo, variables);
		}
	}
	
	/**
	 * E.g. <b>AnoleLogger.info("{}-{}","a","b")</b> will output "a-b"
	 */
	public void info(String baseInfo, Object ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.INFO, "[INFO] " + baseInfo, variables);
		}
		else{ 
			getLogger().info(baseInfo, variables);
		}
	}

	/**
	 * E.g. <b>AnoleLogger.warn("{}-{}","a","b")</b> will output "a-b"
	 */
	public void warn(String baseInfo, Object ... variables){
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
	public void error(String baseInfo, Object ... variables){
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
	public void fatal(String baseInfo, String ... variables){
		if(!Anole.initialized){
			coreLog(LogLevel.FATAL, "[FATAL] "+baseInfo, variables);
		}
		else{ 
			getLogger().error(baseInfo, variables);
		}
	}


	private Logger getLogger(){
		if(logger == null){
			synchronized (owner){
				if(logger == null){
					logger = LoggerFactory.getLogger(owner);
				}
			}
		}
		return logger; 
	}
}
