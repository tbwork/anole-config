package com.github.tbwork.anole.loader.core.resource.impl;

import com.github.tbwork.anole.loader.core.model.ConfigFileResource;
import com.github.tbwork.anole.loader.util.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ClasspathResourceLoader extends FileResourceLoader {


    private static final AnoleLogger logger = new AnoleLogger(ClasspathResourceLoader.class);
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
        Set<String> configLocationsUnderUserSpecifiedClasspathes = getConfigLocationsUnderJavaClasspath(configurationFilePaths);
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
        List<String> callerClasspaths = ProjectUtil.getAppClasspaths();
        for (String callerClasspath : callerClasspaths) {
            for(String configLocation : configLocations) {
                String fullPathPattern = PathUtil.uniformAbsolutePath(S.concat(callerClasspath, configLocation));
                fullPathConfigLocations.add(fullPathPattern);
            }
        }
        return fullPathConfigLocations;
    }

    private Set<String> getConfigLocationsUnderJavaClasspath(String ... configLocations) {
        Set<String> fullPathConfigLocations = new HashSet<String>();
        String programPath = ProjectUtil.getProgramPath();
        List<String> candidatePaths = getJavaClasspaths();
        String userSpecifiedPath = System.getProperty("anole.class.path");
        if(S.isNotEmpty(userSpecifiedPath)){
            candidatePaths.addAll(Arrays.stream(userSpecifiedPath.split(",")).map(item->item.trim()).collect(Collectors.toList()));
        }
        for(String path : candidatePaths) {
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


    private List<String> getJavaClasspaths() {
        List<String> paths = new ArrayList<>();

        String classpath = System.getProperty("java.class.path");
        if (classpath != null) {
            for (String path : classpath.split(File.pathSeparator)) {
                paths.add(path);
            }
        }

        return paths;
    }

    private Path getPathForResource(String resource) {
        URL resourceUrl = this.getClass().getResource(resource);
        if (resourceUrl != null) {
            try {
                return Paths.get(resourceUrl.toURI()).getParent();
            } catch (URISyntaxException e) {
                logger.error("Exceptions thrown in getting class paths...");
                throw  new RuntimeException(e.getMessage());
            }
        }
        return null;
    }

    private Path getCurrentJarPath() {
        URL jarUrl = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        if (jarUrl != null) {
            try {
                return Paths.get(jarUrl.toURI()).getParent();
            } catch (URISyntaxException e) {
                logger.error("Exceptions thrown in getting class paths...");
                throw  new RuntimeException(e.getMessage());
            }
        }
        return null;
    }
}
