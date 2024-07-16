package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.loader.context.AnoleContext;
import com.github.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext;
import com.github.tbwork.anole.loader.spiext.AnoleStartPostProcessor;
import com.github.tbwork.anole.loader.spiext.SpiExtensionManager;
import com.github.tbwork.anole.loader.statics.BuiltInConfigKeys;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.ProjectUtil;
import com.github.tbwork.anole.loader.util.S;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

public class AnoleApp {
  
	private static boolean runingInJar;
	
	private static Class<?> rootMainClass;
	
	private static Class<?> callerClass;
	
	private static String environment;

	private static AnoleContext anoleContext = null;

	private static final AnoleLogger logger = new AnoleLogger(AnoleApp.class);

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
		String [] anoleConfigLocations = {"*.anole","*.properties"};
		String [] includeClassPathDirectoryPatterns = {};
		String [] excludeClassPathDirectoryPatterns = {};
		if(targetRootClass!=null && targetRootClass.isAnnotationPresent(AnoleConfigLocation.class)){
			AnoleConfigLocation anoleConfig = targetRootClass.getAnnotation(AnoleConfigLocation.class);
			anoleConfigLocations = anoleConfig.locations();
			includeClassPathDirectoryPatterns = anoleConfig.includeClassPathDirectoryPatterns();
			excludeClassPathDirectoryPatterns = anoleConfig.excludeClassPathDirectoryPatterns();
		}

		SpiExtensionManager.loadExtensionsFromSpi();

		anoleContext = new AnoleClasspathConfigContext(anoleConfigLocations
				, includeClassPathDirectoryPatterns
				, excludeClassPathDirectoryPatterns
		);

		for(AnoleStartPostProcessor anoleStartPostProcessor : SpiExtensionManager.anoleStartPostProcessors){
				anoleStartPostProcessor.process();
		}
	}

	/**
	 * Start an Anole application with default log level.
	 */
	public static void start(){
		start(AnoleLogger.LogLevel.INFO);
	}

	public static void start(Class<?> targetRootClass){
		start(targetRootClass, AnoleLogger.LogLevel.INFO);
	}

	public static boolean runingInJar(){
		return runingInJar;
	}
	
	public static void setRuningInJar(boolean runingInJar){
		AnoleApp.runingInJar = runingInJar;
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
	 * a main method calling the Anole boot class ({@link AnoleApp}, etc.)
	 * directly.
	 */
	public static Class<?> getCallerClass(){ 
		if(callerClass == null) {
			callerClass = getCallerClassByStackTrace();
		}
		return callerClass; 
	}
	
	public static String getCurrentEnvironment(){
		String env = Anole.getProperty(BuiltInConfigKeys.ANOLE_ENV);

		if(S.isNotEmpty(env)){
			return env;
		}

		env = Anole.getProperty(BuiltInConfigKeys.ANOLE_ENV_SHORT);

		if(S.isNotEmpty(env)){
			return env;
		}

		return Anole.getProperty(BuiltInConfigKeys.ANOLE_ENV_SHORT_CAMEL);

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
				if(stackTraceClass.equals("com.github.tbwork.anole.loader.AnoleApp")
				|| stackTraceClass.equals("com.github.tbwork.anole.loader.context.impl.AnoleFileConfigContext")
				|| stackTraceClass.equals("com.github.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext")
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
			for (int i = 0; i < stackTrace.length ; i ++) {
				if("com.github.tbwork.anole.loader.AnoleApp".equals(stackTrace[i].getClassName())) {
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
