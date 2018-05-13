package org.tbwork.anole.loader.core.loader.impl;
 
import java.util.HashSet;
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
	
	private AnoleLogger logger;
	
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
		Set<String> configLocationPathInALlClasspaths = getConfigLocationPathInAllClasspath(configLocations); 
		if(!testMode) filterTestClasspath(configLocationPathInALlClasspaths);    
		Map<String,FileLoadStatus> loadResult = super.load(logLevel, CollectionUtil.set2StringArray(configLocationPathInALlClasspaths));
		Anole.initialized = true;
		return loadResult;
	}
   
	
	private static Set<String> getConfigLocationPathInAllClasspath(String ... configLocations) {
		Set<String> fullPathConfigLocations = new HashSet<String>();
		String applicationClasspath = ProjectUtil.getApplicationClasspath(); 
	    //get all classpathes
		String classPath = System.getProperty("java.class.path");
		String  [] pathElements = classPath.split(System.getProperty("path.separator")); 
		for(String path : pathElements) {
			path = FileUtil.format2Slash(path);
			if(!FileUtil.isAbsolutePath(path)){
				// Suffix with the root path if the current path is not an absolute path
				if(path.equals("./") || path.equals(".")) { // for current directory
					path = applicationClasspath; 
				} 
				else {
					path = applicationClasspath + path; 
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
	    // In the situation like Spring-Boot, the class-path is not the path where the jar is running under, but somewhere inner managed by the framework itself. 
		for(String configLocation : configLocations) {
			fullPathConfigLocations.add( ProjectUtil.getUserClasspath() + configLocation); 
		} 
		return fullPathConfigLocations;
	}
	
	private void filterTestClasspath(Set<String> classpathFullPaths) {
		for(String classpathFullPath : classpathFullPaths) {
			classpathFullPath = classpathFullPath.replace("test-classes", "classes");
		}
	}
	
	
}
