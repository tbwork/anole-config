package org.tbwork.anole.loader.context.impl;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.AnoleContext;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.OsUtil;
import org.tbwork.anole.loader.util.PathUtil;
import org.tbwork.anole.loader.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnoleContext implements AnoleContext {

    private final AnoleLogger logger = new AnoleLogger(getClass());

    private String [] configLocations;

    protected String environment;

    public AbstractAnoleContext(String [] configLocations) {
        this.configLocations = configLocations;
        this.environment = getCurrentEnvironment();
    }


    protected String [] getConfigLocations() {
        return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
    }

    protected abstract String [] getDefaultConfigLocations();


    /**
     * Create the anole context, this work is defined by sub-classes.
     */
    protected abstract void create();


    @Override
    public void close(){
        AnoleConfigManager.getInstance().stopUpdateManager();
    }

    @Override
    public String getEnvironment() {
        return environment;
    }

    private String getCurrentEnvironment(){
        // check by the following order
        // 1. the jvm system property
        // 2. the os system property
        // 3. the environment file
        //check if the environment is already set or not
        String environment = System.getProperty("anole.env");
        if(StringUtil.isNullOrEmpty(environment)){
            environment = System.getProperty("anole.environment");
        }
        if(StringUtil.isNullOrEmpty(environment)){
            environment = System.getenv("ANOLE_ENV");
        }
        if(StringUtil.isNullOrEmpty(environment)){
            environment = System.getenv("ANOLE_ENVIRONMENT");
        }

        if(StringUtil.isNullOrEmpty(environment)){
            environment = getEnvFromFile();
        }

        if(StringUtil.isNotEmpty(environment)) {
            return environment;
        }

        //throw new EnvironmentNotSetException();
        // from 1.2.5 use info instead and return "all" environment.
        logger.info("Cound not decide current environment, 'all' environment will be used.");

        return "all";
    }


    private String getEnvFromFile(){
        switch(OsUtil.getOsCategory()){
            case WINDOWS:{
                return getEnvFromFile("C://anole/");
            }
            case LINUX:{
                return getEnvFromFile("/etc/anole/");
            }
            case MAC:{
                return getEnvFromFile("/Users/anole/");
            }
            default: return null;
        }
    }

    private String getEnvFromFile(String directoryPath){
        File file = new File(directoryPath);
        if(file.exists()){
            File [] fileList = file.listFiles();
            for(File ifile : fileList){
                String ifname = ifile.getName();
                if(StringUtil.asteriskMatch("*.env", ifname)){
                    return ifname.replace(".env", "");
                }
            }
        }
        return null;
    }

}
