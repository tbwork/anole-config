package org.tbwork.anole.loader.core.register;

import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.Anole;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.loader.core.manager.source.SourceRetriever;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.statics.BuiltInConfigKeyBook;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.ProjectUtil;

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

        logger.info("Local configurations are loaded successfully. Slf4j loggers is initializing...");

        // Initialize the slf4j logger factory.
        LoggerFactory.getLogger(AnoleConfigRegister.class);

        logger.info("Slf4j loggers initialized successfully.");

        // start up the update recorder to prepare to receive update events from the remote config servers.
        lcm.startReceiveIncomeUpdates();

        // initialize extended config source
        initializeExtendedConfigSource();

        // refresh all properties
        lcm.refresh(true);

        // start up the update executor to process update events from the remote config servers.
        lcm.startProcessIncomeUpdates();

        // start to receive outgo update events
        lcm.startReceiveOutgoUpdates();

        // start to process outgo update events
        lcm.startProcessOutgoUpdates();

        // remove anole configurations from system
        if("true".equals(Anole.getRawValue(BuiltInConfigKeyBook.CLEAN_SYSTEM_PROPERTY_AFTER_INITIALIZATION))){
            lcm.removeFromSystem();
        }
    }


    private void initializeExtendedConfigSource(){
        Set<SourceRetriever> extensionSourceSet = lookForRemoteServerBySpi();
        logger.info("There are {} extension config providers found. Details: >>>>>>>>> ", extensionSourceSet.size());
        for(SourceRetriever retriever :extensionSourceSet){
            logger.info("{} is found.", retriever.getName());
            lcm.addExtensionRetriever(retriever);
        }
        if(extensionSourceSet.size() > 0){
            logger.info("Extension config providers are loaded successfully. <<<<<<<<<< ", extensionSourceSet.size());
        }
    }

    private Set<SourceRetriever> lookForRemoteServerBySpi(){

        Set<SourceRetriever> providers = new TreeSet<SourceRetriever>(new Comparator<SourceRetriever>(){
            @Override
            public int compare(SourceRetriever o1, SourceRetriever o2) {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });

        for (final ClassLoader classLoader : ProjectUtil.getClassLoaders()) {
            try {
                for (final SourceRetriever provider : ServiceLoader.load(SourceRetriever.class, classLoader)) {
                    providers.add(provider);
                }
            } catch (final Throwable ex) {
                logger.warn("There is something wrong occurred in provider lookup step. Details: {}", ex.getMessage());
            }
        }

        return providers;

    }




}
