package org.tbwork.anole.loader.context;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.StringUtil;

public class AnoleApp {
  
	public static void start(AnoleLogger.LogLevel logLevel){
		Class<?> runtimeClass =  getAnoleRootClassByStackTrace(); 
		AnoleConfigContext accc = null;
		if(runtimeClass!=null && runtimeClass.isAnnotationPresent(AnoleConfigLocation.class)){
			AnoleConfigLocation anoleConfigFiles = runtimeClass.getAnnotation(AnoleConfigLocation.class); 
			if(!anoleConfigFiles.locations().isEmpty()){
				String [] path = anoleConfigFiles.locations().split(",");
				accc = new AnoleClasspathConfigContext(logLevel, StringUtil.trimStrings(path)); 
				return;
			}  
		}  
		accc = new AnoleClasspathConfigContext(logLevel, "*.anole"); 
	}
	
	public static void start(){
		start(AnoleLogger.defaultLogLevel);
	}
	
	private static Class<?> getAnoleRootClassByStackTrace(){
		try {
			StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
			for (int i =0; i < stackTrace.length ; i++) {
				if("org.tbwork.anole.loader.context.AnoleApp".equals(stackTrace[i].getClassName())) {
					return Class.forName(stackTrace[i+2].getClassName());
				} 
			}
			throw new ClassNotFoundException("Can not find anole's root class, please check your start codes.");
		}
		catch (ClassNotFoundException ex) {
			// Swallow and continue
		}
		return null;
	} 
}
