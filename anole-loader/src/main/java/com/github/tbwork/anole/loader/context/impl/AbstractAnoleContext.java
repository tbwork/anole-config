package com.github.tbwork.anole.loader.context.impl;

import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.context.AnoleContext;
import com.github.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.OsUtil;
import com.github.tbwork.anole.loader.util.S;

import java.io.File;

import static com.github.tbwork.anole.loader.statics.BuiltInConfigKeys.*;

public abstract class AbstractAnoleContext implements AnoleContext {

    private final AnoleLogger logger = new AnoleLogger(getClass());

    private String [] configLocations;

    protected String environment;

    public AbstractAnoleContext(String [] configLocations) {
        this.configLocations = configLocations;
        this.environment = getCurrentEnvironment();
        Anole.setProperty("anole.env", environment);
        Anole.setProperty("anole.environment", environment);
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
    public String getEnvironment() {
        return environment;
    }

    private String getCurrentEnvironment(){

        String environment = System.getenv(ANOLE_ENV);
        if(S.isEmpty(environment)){
            environment = System.getProperty(ANOLE_ENV);
        }
        if(S.isEmpty(environment)){
            environment = System.getenv(ANOLE_ENV_SHORT);
        }
        if(S.isEmpty(environment)){
            environment = System.getProperty(ANOLE_ENV_SHORT);
        }

        if(S.isEmpty(environment)){
            environment = System.getenv(ANOLE_ENV_SHORT_CAMEL);
        }

        if(S.isEmpty(environment)){
            environment = System.getProperty(ANOLE_ENV_SHORT_CAMEL);
        }

        if(S.isEmpty(environment)){
            environment = getEnvFromFile();
        }

        if(S.isNotEmpty(environment)) {
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
                return getEnvFromFile("/etc/anole/");
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
                if(S.asteriskMatch("*.env", ifname)){
                    return ifname.replace(".env", "");
                }
            }
        }
        return null;
    }

}
