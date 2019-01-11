package org.tbwork.anole.loader.core.loader.impl;
 
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.CollectionUtil;
import org.tbwork.anole.loader.util.FileUtil;
import org.tbwork.anole.loader.util.ProjectUtil;

import java.util.*;

public class AnoleClasspathLoader extends AnoleFileLoader{ 
	   
	public AnoleClasspathLoader(){
		super();
	}
	  
	public AnoleClasspathLoader(ConfigManager cm){
		super(cm);
	}
	
	@Override
	public Map<String,FileLoadStatus> load() { 
		return load("*.anole"); 
	}
	
	@Override
	public Map<String,FileLoadStatus> load(String... configLocations) { 
		// User specified classpath.
		Set<String> configLocationsUnderUserSpecifiedClasspathes = getConfigLocationsUnderUserSpecifiedClasspath(configLocations);  
		Set<String> configLocationUnderApplicationClasspathes = getConfigLocationUnderCallerClasspath(configLocations);  
		// remove duplicate path
		for(String configLocationUnderApplicationClasspath : configLocationUnderApplicationClasspathes) {
			configLocationsUnderUserSpecifiedClasspathes.remove(configLocationUnderApplicationClasspath); 
		}
		List<String> orderedConfigLocations = new ArrayList<String>();  
		orderedConfigLocations.addAll(configLocationsUnderUserSpecifiedClasspathes);
		orderedConfigLocations.addAll(configLocationUnderApplicationClasspathes);     
		Map<String,FileLoadStatus> loadResult = super.load(CollectionUtil.list2StringArray(orderedConfigLocations)); 
		return loadResult;
	}
   
	
	/**
	 *  Get configuration locations under the caller's classpath.<br> 
	 */
	private static Set<String> getConfigLocationUnderCallerClasspath(String ... configLocations) {
		Set<String> fullPathConfigLocations = new HashSet<String>();
		String callerClasspath = ProjectUtil.getCallerClasspath();  
		for(String configLocation : configLocations) {
			fullPathConfigLocations.add(callerClasspath+configLocation);
		}
		return fullPathConfigLocations;
	}
	
	private Set<String> getConfigLocationsUnderUserSpecifiedClasspath(String ... configLocations) {
		Set<String> fullPathConfigLocations = new HashSet<String>();
		String programPath = ProjectUtil.getProgramPath();  
	    //get all classpathes
		String classPath = System.getProperty("java.class.path"); 
		String  [] pathElements = classPath.split(System.getProperty("path.separator")); 
		for(String path : pathElements) {
			path = FileUtil.format2Slash(path);
			if(!isInValidScanJar(path)){
				continue;
			}
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
			if(!path.startsWith("/")) //uniform the form of absolute path
				path = "/" + path;
			for(String configLocation : configLocations) {
				fullPathConfigLocations.add( path + configLocation); 
			}
		}
		return fullPathConfigLocations;
	} 
	 
}
