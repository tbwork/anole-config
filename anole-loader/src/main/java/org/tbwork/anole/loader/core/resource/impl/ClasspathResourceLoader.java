package org.tbwork.anole.loader.core.resource.impl;

import org.tbwork.anole.loader.core.model.ConfigFileResource;
import org.tbwork.anole.loader.util.*;

import java.util.*;
import java.util.stream.Collectors;

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
        String callerClasspath = ProjectUtil.getAppClasspath();
        for(String configLocation : configLocations) {
            String fullPathPattern = PathUtil.uniformAbsolutePath(S.concat(callerClasspath, configLocation));
            fullPathConfigLocations.add(fullPathPattern);
        }
        return fullPathConfigLocations;
    }

    private Set<String> getConfigLocationsUnderUserSpecifiedClasspath(String ... configLocations) {
        Set<String> fullPathConfigLocations = new HashSet<String>();
        String programPath = ProjectUtil.getProgramPath();
        //get all classpathes
        String classPath = System.getProperty("java.class.path");
        List<String> pathElements = Arrays.stream(classPath.split(System.getProperty("path.separator"))).map(item->item.trim()).collect(Collectors.toList());
        String userSpecifiedPath = System.getProperty("anole.class.path");
        if(S.isNotEmpty(userSpecifiedPath)){
            pathElements.addAll(Arrays.stream(userSpecifiedPath.split(",")).map(item->item.trim()).collect(Collectors.toList()));
        }
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

            if(needTobeFiltered(path))
                continue;

            for(String configLocation : configLocations) {
                String fullPathPattern = PathUtil.uniformAbsolutePath(S.concat(path, configLocation));
                fullPathConfigLocations.add(fullPathPattern);
            }
        }
        return fullPathConfigLocations;
    }




    private boolean needTobeFiltered(String configLocation){
        boolean includedMatch = true; // if the include path is not set, all paths are included.
        if(includedPatterns.length > 0){
            includedMatch = false; // once the include path is set, only matched path is valid.
            // filter those paths which does not match the includedPatterns.
            for(String pattern : includedPatterns){
                if( PathUtil.directoryMatch(configLocation, pattern)){
                    includedMatch = true; // do not need to be excluded.
                }
            }

        }
        boolean excludedMatch = false; // if the exclude path is not set, no path is excluded.
        if(excludePatterns.length > 0){
            // filter those paths which does match the excludePatterns.
            for(String pattern : excludePatterns){
                if( PathUtil.directoryMatch(configLocation, pattern)){
                    excludedMatch =  true; // need to be excluded
                }
            }
        }

        return excludedMatch || (!excludedMatch && !includedMatch);
    }

    /**
     * @param fullpath like "/a/b/c/d"
     * @param part like "/a/*b/c/"
     * @return true if matched, otherwise return false
     */
    private boolean match(String fullpath, String part){
        part = S.concat("*", part, "*");
        return  S.asteriskMatch(part, fullpath);
    }


    private String toDirectoryPath(String path){
        String result = path.startsWith("/") ? path : S.concat("/", path);
        result = result.endsWith("/") ? result : S.concat(result, "/");
        return result;
    }
}
