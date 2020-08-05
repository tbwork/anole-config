package org.tbwork.anole.loader.core.resource.impl;

import org.tbwork.anole.loader.core.model.ConfigFileResource;
import org.tbwork.anole.loader.core.resource.ResourceLoader;
import org.tbwork.anole.loader.util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClasspathResourceLoader extends FileResourceLoader {


    private static final  AnoleLogger  logger = new AnoleLogger(ClasspathResourceLoader.class);
    /**
     * Whitelist
     */
    private String [] includedPatterns;
    /**
     * Blacklist
     */
    private String [] excludePatterns;

    public ClasspathResourceLoader(String [] includedPatterns, String [] excludePatterns) {
        this.includedPatterns = includedPatterns;
        this.excludePatterns = excludePatterns;
    }

    @Override
    public ConfigFileResource[] load(String... configurationFilePaths) {
        // User specified classpath.
        Set<String> configLocationsUnderUserSpecifiedClasspathes = getConfigLocationsUnderUserSpecifiedClasspath(configurationFilePaths);
        Set<String> configLocationUnderApplicationClasspathes = getConfigLocationUnderCallerClasspath(configurationFilePaths);
        // remove duplicate path
        for(String configLocationUnderApplicationClasspath : configLocationUnderApplicationClasspathes) {
            configLocationsUnderUserSpecifiedClasspathes.remove(configLocationUnderApplicationClasspath);
        }
        List<String> orderedConfigLocations = new ArrayList<String>();
        orderedConfigLocations.addAll(configLocationsUnderUserSpecifiedClasspathes);
        orderedConfigLocations.addAll(configLocationUnderApplicationClasspathes);
        if(logger.isDebugEnabled()){
            logger.debug("All patterns will be matched are:");
            int p = 0;
            for(String configLocation : orderedConfigLocations){
                logger.debug("{} - {}", p++, configLocation);
            }
        }
        return super.load(CollectionUtil.list2StringArray(orderedConfigLocations));
    }



    /**
     *  Get configuration locations under the caller's classpath.<br>
     */
    private static Set<String> getConfigLocationUnderCallerClasspath(String ... configLocations) {
        Set<String> fullPathConfigLocations = new HashSet<String>();
        String callerClasspath = ProjectUtil.getCallerClasspath();
        for(String configLocation : configLocations) {
            String fullPathPattern = PathUtil.uniformAbsolutePath(StringUtil.concat(callerClasspath, configLocation));
            fullPathConfigLocations.add(fullPathPattern);
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

            if(filterUseless(path))
                continue;

            for(String configLocation : configLocations) {
                String fullPathPattern = PathUtil.uniformAbsolutePath(StringUtil.concat(path, configLocation));
                fullPathConfigLocations.add(fullPathPattern);
            }
        }
        return fullPathConfigLocations;
    }




    private boolean filterUseless(String configLocation){
        if(includedPatterns.length > 0){
            // filter those paths which does not match the includedPatterns.
            for(String pattern : includedPatterns){
                if( PathUtil.directoryMatch(configLocation, pattern)){
                    return true;
                }
            }
            return false;
        }
        if(excludePatterns.length > 0){
            // filter those paths which does match the excludePatterns.
            for(String pattern : excludePatterns){
                if( PathUtil.directoryMatch(configLocation, pattern)){
                    return false;
                }
            }
            return true;
        }
        // means all configuration location are valid and useful.
        return false;
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
