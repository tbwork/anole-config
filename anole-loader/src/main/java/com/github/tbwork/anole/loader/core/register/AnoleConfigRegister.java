package com.github.tbwork.anole.loader.core.register;

import com.github.tbwork.anole.loader.core.manager.source.ConfigSource;
import com.github.tbwork.anole.loader.core.model.RawKV;
import com.github.tbwork.anole.loader.statics.BuiltInConfigKeys;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.ProjectUtil;
import org.slf4j.LoggerFactory;
import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.core.manager.ConfigManager;
import com.github.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;

import java.util.*;

public class AnoleConfigRegister {

    private static final AnoleLogger logger = new AnoleLogger(AnoleConfigRegister.class);

    private ConfigManager lcm;


    /**
     * Register all KVs to the config manager center.
     * @param rawKVList
     */
    public void register(List<RawKV> rawKVList){

        lcm = AnoleConfigManager.getInstance();

        // register raw definition
        lcm.batchRegisterDefinition(rawKVList);

        // refresh configs locally
        lcm.refresh(false);

        // register to system for other framework to read.
        lcm.registerToSystem();

        // initialize extended config source
        initializeExtendedConfigSource();

        // refresh all properties
        if(Anole.getBoolProperty(BuiltInConfigKeys.ANOLE_STRICT_MODE, false)){
            lcm.refresh(true);
        }
        else{
            lcm.refresh(false);
        }

        // remove anole configurations from system
        if("true".equals(Anole.getRawValue(BuiltInConfigKeys.CLEAN_SYSTEM_PROPERTY_AFTER_INITIALIZATION))){
            lcm.removeFromSystem();
        }
    }


    private void initializeExtendedConfigSource(){
        Set<ConfigSource> extensionSourceSet = lookForRemoteServerBySpi();
        logger.info("There are {} extension config providers found. Details: >>>>>>>>> ", extensionSourceSet.size());
        for(ConfigSource retriever :extensionSourceSet){
            logger.info("{} is found.", retriever.getName());
            lcm.addExtensionRetriever(retriever);
        }
        if(extensionSourceSet.size() > 0){
            logger.info("Extension config providers are loaded successfully. <<<<<<<<<< ", extensionSourceSet.size());
        }
    }

    private Set<ConfigSource> lookForRemoteServerBySpi(){

        Set<ConfigSource> providers = new TreeSet<>(Comparator.comparing(o -> o.getClass().getName()));

        for (final ClassLoader classLoader : ProjectUtil.getClassLoaders()) {
            try {
                for (final ConfigSource provider : ServiceLoader.load(ConfigSource.class, classLoader)) {
                    providers.add(provider);
                }
            } catch (final Throwable ex) {
                logger.warn("There is something wrong occurred in provider lookup step. Details: {}", ex.getMessage());
            }
        }

        return providers;

    }




}
