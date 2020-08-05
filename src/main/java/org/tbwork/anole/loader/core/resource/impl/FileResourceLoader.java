package org.tbwork.anole.loader.core.resource.impl;

import lombok.Data;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.core.loader.impl.AnoleFileLoader;
import org.tbwork.anole.loader.core.model.ConfigFileResource;
import org.tbwork.anole.loader.core.resource.ResourceLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.*;
import sun.security.provider.ConfigFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class FileResourceLoader implements ResourceLoader {


    private static final AnoleLogger logger = new AnoleLogger(FileResourceLoader.class);

    private static final List<String> projectInfoPropertiesInJarPathList  = new ArrayList<String>();
    private static final List<String> projectInfoPropertiesPathList  = new ArrayList<String>();
    static {
        projectInfoPropertiesPathList.add("../maven-archiver/pom.properties");
        projectInfoPropertiesPathList.add("META-INF/maven/*/*/pom.properties");
        projectInfoPropertiesInJarPathList.add("META-INF/maven/*/*/pom.properties");
    }

    public FileResourceLoader(){
    }



    @Override
    public ConfigFileResource [] load(String... configurationFilePaths) {

        AnoleApp.setRuningInJar(ProjectUtil.getCallerClasspath().contains(".jar!"));
        logger.debug("Current environment is {}", AnoleApp.getEnvironment());
        List<ConfigFileResource> result = new ArrayList<ConfigFileResource>();

        Map<String, Integer> candidates = new HashMap<String, Integer>();

        // set loading order
        for(String configurationFilePath : configurationFilePaths) {

            if(configurationFilePath.startsWith(ProjectUtil.getCallerClasspath())){
                // inner project, load at the end
                candidates.put(configurationFilePath, 99);
            }
            else {
                // outer path
                if(configurationFilePath.contains(".jar/")){
                    // outer jars
                    candidates.put(configurationFilePath, 1);
                }
                else{
                    // outer directory
                    candidates.put(configurationFilePath, 50);
                }
            }
        }
        for(String projectInfoFile : getFullPathForProjectInfoFiles()) {
            candidates.put(projectInfoFile, 10);
        }
        for(Map.Entry<String, Integer> entry : candidates.entrySet()) {
            List<ConfigFileResource> tempResult = loadFile(  entry.getKey(),  entry.getValue());
            if(tempResult.isEmpty()){
                logger.debug("Pattern <{}> matches nothing !", entry.getKey());
            }
            else{
                // log matched paths
                logger.info("Pattern <{}> matches : ",
                        entry.getKey());
                for(ConfigFileResource item :tempResult){
                    logger.info(item.getFullPath());
                }
            }
            result.addAll(tempResult);
        }

        return result.toArray(new ConfigFileResource[result.size()]);
    }




    private List<String> getFullPathForProjectInfoFiles() {
        List<String> result = new ArrayList<String>();
        String projectInfoPath =  ProjectUtil.getCallerClasspath();
        projectInfoPath = projectInfoPath.replace("test-classes", "classes");
        if(projectInfoPath.contains(".jar!/")) {
            int index = projectInfoPath.indexOf(".jar!/");
            projectInfoPath = projectInfoPath.substring(0, index+6);
            for(String projectInfoFile : projectInfoPropertiesInJarPathList) {
                result.add(projectInfoPath + projectInfoFile);
            }
        }
        else {
            for(String projectInfoFile : projectInfoPropertiesPathList) {
                result.add(projectInfoPath + projectInfoFile);
            }
        }
        return result;
    }



    /**
     * @param patternedPath patterned file path
     */
    private List<ConfigFileResource> loadFile(String patternedPath, int order){
        logger.debug("Searching config files matchs '{}'", patternedPath);

        if(patternedPath.contains("!/")){ // For Jar projects
            return loadFileFromJar(patternedPath, order);
        }
        else{
            return loadFileFromDirectory(patternedPath, order);
        }
    }

    private List<ConfigFileResource> loadFileFromJar(String patternedPath, int order){
        List<ConfigFileResource> result = new ArrayList<>();
        Map<String, InputStream>  fileStreamMap = FileUtil.loadFileStreamFromJar(patternedPath);
        for(Map.Entry<String, InputStream> entry : fileStreamMap.entrySet()){
            logger.debug("New configuration file is found, name : {}", entry.getKey());
            result.add( new ConfigFileResource( entry.getKey(),  entry.getValue(),
                    order));
        }
        return result;
    }

    private List<ConfigFileResource> loadFileFromDirectory(String patternedPath, int order){
        List<ConfigFileResource> result = new ArrayList<>();
        Map<String, File> fileMap = FileUtil.loadFileByPatternedPath(patternedPath);
        for(Map.Entry<String, File> entry : fileMap.entrySet()){
            logger.debug("New configuration file is found, name : {}", entry.getKey());
            result.add( new ConfigFileResource( entry.getKey(), FileUtil.getInputStream( entry.getValue()),
                    order));
        }
        return result;
    }

    private static boolean isProjectInfo(String fileFullPath) {
        for(String path : projectInfoPropertiesInJarPathList) {
            if(fileFullPath.contains(path))
                return true;
        }
        for(String path : projectInfoPropertiesPathList) {
            if(fileFullPath.contains(path))
                return true;
        }
        return false;
    }



}
