package org.tbwork.anole.loader.context;

import org.tbwork.anole.loader.annotion.AnoleClassPathFilter;
import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext;
import org.tbwork.anole.loader.core.loader.impl.AnoleCallBack;
import org.tbwork.anole.loader.core.manager.impl.LocalConfigManager;
import org.tbwork.anole.loader.exceptions.BadFileException;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.SingletonFactory;
import org.tbwork.anole.loader.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class AnoleApp {
  
	private static boolean runingInJar;
	
	private static Class<?> rootMainClass;
	
	private static Class<?> callerClass;
	
	private static String environment;

	private static final LocalConfigManager lcm = SingletonFactory.getLocalConfigManager();

	/**
	 * Start an anole application.
	 * @param logLevel the logLevel of anole itself.
	 */
	public static void start(AnoleLogger.LogLevel logLevel, AnoleCallBack anoleCallBack){
		Class<?> runtimeClass =  getAnoleRootClassByStackTrace(); 
		start(runtimeClass, logLevel, anoleCallBack);
	}
	
	/**
	 * Start an Anole application with specified root class.
	 * @param targetRootClass the root start class.
	 * @param logLevel the logLevel of Anole itself.
	 */
	public static void start(Class<?> targetRootClass, AnoleLogger.LogLevel logLevel, AnoleCallBack anoleCallBack) {
		AnoleLogger.anoleLogLevel = logLevel;
		AnoleClassPathFilter classPathFilter = null;
		if(targetRootClass!=null && targetRootClass.isAnnotationPresent(AnoleClassPathFilter.class)){
			classPathFilter = targetRootClass.getAnnotation(AnoleClassPathFilter.class);
		}
		if(targetRootClass!=null && targetRootClass.isAnnotationPresent(AnoleConfigLocation.class)){
			AnoleConfigLocation anoleConfigFiles = targetRootClass.getAnnotation(AnoleConfigLocation.class); 
			if(anoleConfigFiles.locations() != null &&  anoleConfigFiles.locations().length > 0){
				new AnoleClasspathConfigContext(classPathFilter, StringUtil.trimStrings(anoleConfigFiles.locations()));
				return;
			}  
		}  
		new AnoleClasspathConfigContext(classPathFilter);
	}

	/**
	 * Start an Anole application with default log level and a specified callback.
	 */
	public static void start(AnoleLogger.LogLevel logLevel){
		start(logLevel, null);
	}

	/**
	 * Start an Anole application with default log level and a specified callback.
	 */
	public static void start(AnoleCallBack anoleCallBack){
		start(AnoleLogger.defaultLogLevel, anoleCallBack);
	}

	/**
	 * Start an Anole application with default log level.
	 */
	public static void start(){
		start(AnoleLogger.defaultLogLevel, null);
	}

	public static boolean runingInJar(){
		return runingInJar;
	}
	
	public static void setRuningInJar(boolean runingInJar){
		AnoleApp.runingInJar = runingInJar;
	}


	/**
	 * @param env the specified environment like: production
	 */
	public static void setEnvironment(String env) {
		environment = env;
	}

	/**
	 * Specify an input stream whose content is like:<br>
	 * <pre>
	 * 	environment=dev
	 * </pre>
	 */
	public static void setEnvironment(InputStream is){
		parse(is);
	}

	/**
	 * Please specify a class path file's path like "env.anole" whose content is about:<br>
	 * <pre>
	 * 	environment=dev
	 * </pre>
	 */
	public static void setEnvironmentFromClassPathFile(String classPathFile) {
		if(!classPathFile.startsWith("/"))
			classPathFile = StringUtil.concat("/", classPathFile);
		setEnvironment(AnoleApp.class.getResourceAsStream(classPathFile));
	}

	/**
	 * Specify a anole environment file whose content is like:<br>
	 * <pre>
	 *  environment=dev
	 * </pre>
	 */
	public static void setEnvironment(File file){
		try{
			FileInputStream fileInputStream = new FileInputStream(file);
			setEnvironment(fileInputStream);
		}
		catch (FileNotFoundException e){
			throw new BadFileException("The environment file is broken or invalid.");
		}
		catch (Exception e){
			throw new BadFileException(e.getMessage());
		}

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
	 * a main method calling the Anole boot class ({@link AnoleApp}, etc.)
	 * directly.
	 */
	public static Class<?> getCallerClass(){ 
		if(callerClass == null) {
			callerClass = getCallerClassByStackTrace();
		}
		return callerClass; 
	}

	/**
	 * <p> For <b>maven</b> projects, this method will return the artifactId.
	 * <p> For <b>other</b> projects, this method will return the value of 
	 * variable named "anole.project.info.name", you should define it first in your
	 * configuration files.
	 * <p>Note: If you want to use this method successfully, you should better
	 * guarantee that the XXXClass.main(...) method need to be put in the target
	 * project.
	 * </p>
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

	public static void putLocalProperty(String key, String value){
		lcm.setConfigItem(key, value, ConfigType.STRING);
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
				String methodName = stackTraces[i].getMethodName();
				if(methodName.equals("main")) {
					anoleBootClassIndex = i;
					break;
				}
			}
			if(anoleBootClassIndex < stackTraces.length) {
				return Class.forName(stackTraces[anoleBootClassIndex].getClassName());
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
			for (int i = stackTrace.length - 1; i > 0 ; i--) {
				if("org.tbwork.anole.loader.context.AnoleApp".equals(stackTrace[i].getClassName())) {
					return Class.forName(stackTrace[i+1].getClassName());
				} 
			}
			throw new ClassNotFoundException("Can not find anole's root class, please check your start codes.");
		}
		catch (ClassNotFoundException ex) {
			// Swallow and continue
		}
		return null;
	}


	private static void parse(InputStream is) {
		Scanner s = new Scanner(is);
		boolean found = false;
		while(s.hasNextLine()){
			String line =StringUtil.removeBlankChars(s.nextLine());
			if(line.startsWith("env")){
				setEnvironment(line.split("=")[1]);
				found = true;
			}
		}
		if(!found){
			AnoleLogger.warn("It seems that there is no environment information in your input stream: {}",is.toString());
		}
	}

}
