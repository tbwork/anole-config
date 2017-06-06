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
		Class<?> runtimeClass = getRootClassByStackTrace();
		AnoleLoader anoleLoader = new AnoleClasspathLoader(); 
		if(runtimeClass!=null && runtimeClass.isAnnotationPresent(AnoleConfigLocation.class)){
			AnoleConfigLocation anoleConfigFiles = runtimeClass.getAnnotation(AnoleConfigLocation.class); 
			if(!anoleConfigFiles.locations().isEmpty()){
				anoleLoader.load(anoleConfigFiles.locations().split(","));
				return;
			} 
		}
		anoleLoader.load();
	}
	
	private static Class<?> getRootClassByStackTrace(){
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		String className =  elements[elements.length-1].getClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
