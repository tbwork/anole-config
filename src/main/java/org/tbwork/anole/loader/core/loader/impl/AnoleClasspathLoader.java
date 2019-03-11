package org.tbwork.anole.loader.core.loader.impl;

import org.tbwork.anole.loader.annotion.AnoleClassPathFilter;
import org.tbwork.anole.loader.context.impl.MatchCounter;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnoleClasspathLoader extends AnoleFileLoader{ 

	private AnoleClassPathFilter classPathFilter;

	public AnoleClasspathLoader(AnoleClassPathFilter classPathFilter, ConfigManager cm){
		super(cm);
		this.classPathFilter = classPathFilter;
	}

	public AnoleClasspathLoader(AnoleClassPathFilter classPathFilter){
		super();
		this.classPathFilter = classPathFilter;
	}

	public AnoleClasspathLoader(ConfigManager cm){
		super(cm);
	}

	public AnoleClasspathLoader(){
		super();
	}

	@Override
	public void load() {
		load("*.anole");
	}
	
	@Override
	public void load(String... configLocations) {
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
		if(AnoleLogger.isDebugEnabled()){
			AnoleLogger.debug("All patterns will be matched are:");
			int p = 0;
			for(String configLocation : orderedConfigLocations){
			  AnoleLogger.debug("{} - {}", p++, configLocation);
			}
		}
		super.load(CollectionUtil.list2StringArray(orderedConfigLocations));
	}

	/**
	 *  Get configuration locations under the caller's classpath.<br> 
	 */
	private static Set<String> getConfigLocationUnderCallerClasspath(String ... configLocations) {
		Set<String> fullPathConfigLocations = new HashSet<String>();
		String callerClasspath = ProjectUtil.getCallerClasspath();  
		for(String configLocation : configLocations) {
			String fullPathPattern = PathUtil.uniformPath(StringUtil.concat(callerClasspath, configLocation));
			fullPathConfigLocations.add(fullPathPattern);
			MatchCounter.putConfigMap(fullPathPattern, configLocation);
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
			path = PathUtil.format2Slash(path);
			if(!PathUtil.isAbsolutePath(path)){
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

			if(!validateClassPath(path))
				continue;

			for(String configLocation : configLocations) {
				String fullPathPattern = PathUtil.uniformPath(StringUtil.concat(path, configLocation));
				fullPathConfigLocations.add(fullPathPattern);
				MatchCounter.putConfigMap(fullPathPattern, configLocation);
			}
		}
		return fullPathConfigLocations;
	} 


	private boolean validateClassPath(String classpath){
		if(classPathFilter == null)
			return true;
		String [] contains = classPathFilter.contains();
		String [] withouts = classPathFilter.without();
		boolean valid = false;
		for(String contain : contains){
			String directoryCondition = toDirectoryPath(contain);
			if(match(classpath, directoryCondition)){
				valid = true;
				break;
			}
		}

		for(String without : withouts){
			String directoryCondition = toDirectoryPath(without);
			if(match(classpath, directoryCondition)){
				valid = false;
				break;
			}
		}
		return valid;
	}


	/**
	 * @param fullpath like "/a/b/c/d"
	 * @param part like "/a/*b/c/"
	 * @return true if matched, otherwise return false
	 */
	private boolean match(String fullpath, String part){
		 part = StringUtil.concat("*", part, "*");
		return  StringUtil.asteriskMatch(part, fullpath);
	}


	private String toDirectoryPath(String path){
		String result = path.startsWith("/") ? path : StringUtil.concat("/", path);
		result = result.endsWith("/") ? result : StringUtil.concat(result, "/");
		return result;
	}
}
