package org.tbwork.anole.loader.context;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.StringUtil;

public class AnoleApp {
  
	private static boolean runingInJar;
	
	private static Class<?> rootMainClass;
	
	private static Class<?> callerClass;
	
	private static String environment;
	
	/**
	 * Start an anole application.
	 * @param logLevel the logLevel of anole itself.
	 */
	public static void start(AnoleLogger.LogLevel logLevel){
		Class<?> runtimeClass =  getAnoleRootClassByStackTrace(); 
		start(runtimeClass, logLevel);
	}
	
	/**
	 * Start an Anole application with specified root class.
	 * @param targetRootClass the root start class.
	 * @param logLevel the logLevel of Anole itself.
	 */
	public static void start(Class<?> targetRootClass, AnoleLogger.LogLevel logLevel) {
		AnoleLogger.anoleLogLevel = logLevel; 
		if(targetRootClass!=null && targetRootClass.isAnnotationPresent(AnoleConfigLocation.class)){
			AnoleConfigLocation anoleConfigFiles = targetRootClass.getAnnotation(AnoleConfigLocation.class); 
			if(!anoleConfigFiles.locations().isEmpty()){
				String [] path = anoleConfigFiles.locations().split(",");
				new AnoleClasspathConfigContext(StringUtil.trimStrings(path)); 
				return;
			}  
		}  
		new AnoleClasspathConfigContext(); 
	}
	
	/**
	 * Start an Anole application with default log level.
	 */
	public static void start(){
		start(AnoleLogger.defaultLogLevel);
	}

	public static boolean runingInJar(){
		return runingInJar;
	}
	
	public static void setRuningInJar(boolean runingInJar){
		AnoleApp.runingInJar = runingInJar;
	}
	
	 
	public static void setEnvironment(String env) {
		environment = env;
	}
	 
	
	public static String getEnvironment() {
		return environment;
	} 
	
	/**
	 * The root main class in Anole refers to the main class 
	 * of current java application.
	 */
	public static Class<?> getRootMainClass(){ 
		if(rootMainClass == null) {
			rootMainClass = getRootClassByStackTrace();
		}
		return rootMainClass; 
	}
	
	/**
	 * The user main class in Anole refers to the class which contains 
	 * a main method calling the Anole boot class ({@link Anole},{@link AnoleConfigContext}, etc.)
	 * directly.
	 */
	public static Class<?> getCallerClass(){ 
		if(callerClass == null) {
			callerClass = getCallerClassByStackTrace();
		}
		return callerClass; 
	}
	
	public static String getCurrentEnvironment(){
		return Anole.getProperty("anole.runtime.currentEnvironment");
	}
	
	
	/**
	 * <p> For <b>maven</b> projects, this method will return the artifactId.
	 * <p> For <b>other</b> projects, this method will return the value of 
	 * variable named "anole.project.info.name", you should define it first in your
	 * configuration files.
	 * @return the project name
	 */
	public static String getProjectName() {
		String projectName = Anole.getProperty("artifactId");
		if(projectName == null)
			projectName = Anole.getProperty("anole.project.info.name");
		return projectName;
	}
	
	/**
	 * <p> For <b>maven</b> projects, this method will return the version.
	 * <p> For <b>other</b> projects, this method will return the value of 
	 * variable named "anole.project.info.version", you should define it first in your
	 * configuration files.
	 * @return the project version
	 */
	public static String getProjectVersion() {
		String projectVersion = Anole.getProperty("version");
		if(projectVersion == null)
			projectVersion = Anole.getProperty("anole.project.info.version");
		return projectVersion;
	}

	

	private static Class<?> getRootClassByStackTrace(){
		try {
			StackTraceElement[] stackTraces = new RuntimeException().getStackTrace(); 
			if(stackTraces.length > 0)
				return Class.forName(stackTraces[stackTraces.length-1].getClassName());
			throw new ClassNotFoundException("Could not find the root class of current thread");
		}
		catch (ClassNotFoundException ex) {
			// Swallow and continue
			return null;
		} 
	} 
	 
	private static Class<?> getCallerClassByStackTrace(){
		try {
			StackTraceElement[] stackTraces = new RuntimeException().getStackTrace(); 
			int anoleBootClassIndex = stackTraces.length;
			for(int i= stackTraces.length - 1; i >=0 ; i-- ) {
				String stackTraceClass = stackTraces[i].getClassName();
				if(stackTraceClass.equals("org.tbwork.anole.loader.context.AnoleApp") 
				|| stackTraceClass.equals("org.tbwork.anole.loader.context.impl.AnoleFileConfigContext")
				|| stackTraceClass.equals("org.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext")
				) {
					anoleBootClassIndex = i;
					break;
				}
			}
			if(anoleBootClassIndex < stackTraces.length) {
				int targetClassIndex = anoleBootClassIndex;
				if( anoleBootClassIndex != (stackTraces.length-1)) { 
					targetClassIndex = targetClassIndex + 1;
				}
				return Class.forName(stackTraces[targetClassIndex].getClassName());
			}
			else {
				throw new ClassNotFoundException("Could not find any class calling Anole.");
			}
		}
		catch (ClassNotFoundException ex) {
			// Swallow and continue
			return null;
		} 
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
