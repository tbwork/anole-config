package org.tbwork.anole.loader.core;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.util.AnoleLogger;

public class AnoleApp {
 
	
	public static void start(AnoleLogger.LogLevel logLevel){
		Class<?> runtimeClass =  getRootClassByStackTrace();
		AnoleLoader anoleLoader = new AnoleClasspathLoader(); 
		if(runtimeClass!=null && runtimeClass.isAnnotationPresent(AnoleConfigLocation.class)){
			AnoleConfigLocation anoleConfigFiles = runtimeClass.getAnnotation(AnoleConfigLocation.class); 
			if(!anoleConfigFiles.locations().isEmpty()){
				anoleLoader.load(logLevel, anoleConfigFiles.locations().split(","));
				return;
			}  
		} 
		anoleLoader.load(logLevel);
	}
	
	public static void start(){
		start(AnoleLogger.defaultLogLevel);
	}
	
	private static Class<?> getRootClassByStackTrace(){
		try {
			StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				if ("main".equals(stackTraceElement.getMethodName())) {
					return Class.forName(stackTraceElement.getClassName());
				}
			}
		}
		catch (ClassNotFoundException ex) {
			// Swallow and continue
		}
		return null;
	} 
}
