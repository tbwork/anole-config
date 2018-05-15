package org.tbwork.anole.loader.core.loader.impl;
 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;
import org.tbwork.anole.loader.util.CollectionUtil;
import org.tbwork.anole.loader.util.FileUtil;
import org.tbwork.anole.loader.util.ProjectUtil; 

public class AnoleClasspathLoader extends AnoleFileLoader{ 
	 
	private boolean testMode = false; 
	
	
	public AnoleClasspathLoader(){
		super();
	}
	
	
	/**
	 * Used to decide to load configuration files from test class-path.
	 * @param testMode <b>true</b> if you want to load configuration files from test class-path,
	 * <b>otherwise</b> from main class-path.
	 */
	public AnoleClasspathLoader(boolean testMode){
		this.testMode = testMode;
	}
	
	
	public AnoleClasspathLoader(ConfigManager cm){
		super(cm);
	}
	
	@Override
	public Map<String,FileLoadStatus> load(LogLevel logLevel) { 
		AnoleLogger.anoleLogLevel = logLevel; 
		return load(logLevel, "*.anole"); 
	}
	
	@Override
	public Map<String,FileLoadStatus> load(LogLevel logLevel, String... configLocations) { 
		AnoleLogger.anoleLogLevel = logLevel;  
		// User specified classpath.
		Set<String> configLocationsUnderUserSpecifiedClasspathes = getConfigLocationsUnderUserSpecifiedClasspath(configLocations);  
		Set<String> configLocationUnderApplicationClasspathes = getConfigLocationUnderApplicationClasspath(configLocations);
		Set<String> configLocationUnderMainclassClasspath = getConfigLocationUnderMainclassClasspath(configLocations);
		// integrate configLocationUnderApplicationClasspathes with configLocationUnderMainclassClasspath
		configLocationUnderApplicationClasspathes.addAll(configLocationUnderMainclassClasspath);
		// remove duplicate path
		for(String configLocationUnderApplicationClasspath : configLocationUnderApplicationClasspathes) {
			configLocationsUnderUserSpecifiedClasspathes.remove(configLocationUnderApplicationClasspath); 
		}
		List<String> orderedConfigLocations = new ArrayList<String>();  
		orderedConfigLocations.addAll(configLocationsUnderUserSpecifiedClasspathes);
		orderedConfigLocations.addAll(configLocationUnderApplicationClasspathes); 
		if(!testMode) filterTestClasspath(orderedConfigLocations);    
		Map<String,FileLoadStatus> loadResult = super.load(logLevel, CollectionUtil.list2StringArray(orderedConfigLocations));
		Anole.initialized = true;
		return loadResult;
	}
   
	
	/**
	 *  Get configuration locations under application classpath.<br>
	 *  For spring-boot projects, it is "xxx.jar!/BOOT-INF/classes/".<br>
	 *  For java-file projects, it is "... /classes".<br>
	 *  For normal jar-file projects, it is ".../" which is the root directory of current jar.
	 */
	private static Set<String> getConfigLocationUnderApplicationClasspath(String ... configLocations) {
		Set<String> fullPathConfigLocations = new HashSet<String>();
		String applicationClasspath = ProjectUtil.getApplicationClasspath(); 
		for(String configLocation : configLocations) {
			fullPathConfigLocations.add(applicationClasspath+configLocation);
		}
		return fullPathConfigLocations;
	}
	
	/**
	 * Get configuration locations under main-class classpath.<br>
	 * For spring-boot or normal jar-file projects, it is "xxx.jar!/". <br>
	 * For java-file projects(like debug in Eclipse), it is ".../classes" 
	 */
	private static Set<String> getConfigLocationUnderMainclassClasspath(String ... configLocations) {
		Set<String> fullPathConfigLocations = new HashSet<String>();
		String mainclassClasspath = ProjectUtil.getMainclassClasspath(); 
		for(String configLocation : configLocations) {
			fullPathConfigLocations.add(mainclassClasspath+configLocation);
		}
		return fullPathConfigLocations;
	}
	
	private static Set<String> getConfigLocationsUnderUserSpecifiedClasspath(String ... configLocations) {
		Set<String> fullPathConfigLocations = new HashSet<String>();
		String programPath = ProjectUtil.getProgramClasspath();  
	    //get all classpathes
		String classPath = System.getProperty("java.class.path");
		String  [] pathElements = classPath.split(System.getProperty("path.separator")); 
		for(String path : pathElements) {
			path = FileUtil.format2Slash(path);
			if(!FileUtil.isAbsolutePath(path)){
				// Suffix with the root path if the current path is not an absolute path
				if(path.equals("./") || path.equals(".")) { 
					// for current directory
					if(path.equals("./") || path.equals(".")) { // for current directory
						path = programPath; 
					} 
					else {
						path = programPath + path; 
					} 
				} 
				else {
					path = programPath + path; 
				} 
			}
			if(path.endsWith(".jar")) {
				path = path + "!/"; 
			} 
			if(!path.endsWith("/"))
				path = path + "/";
			for(String configLocation : configLocations) {
				fullPathConfigLocations.add( path + configLocation); 
			}
		}
		return fullPathConfigLocations;
	}
	
	private void filterTestClasspath(List<String> classpathFullPaths) {
		for(String classpathFullPath : classpathFullPaths) {
			classpathFullPath = classpathFullPath.replace("test-classes", "classes");
		}
	}
	
	
}
