package org.tbwork.anole.loader.core.register;

import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.loader.core.manager.source.RemoteRetriever;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.util.AnoleLogger;

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

        // remove from system
        lcm.removeFromSystem();

        // start up the update recorder to prepare to receive update events from the remote config servers.
        lcm.startUpdateRecorder();

        // initialize retrievers
        initializeRemoteConfigServer();

        // refresh all properties
        lcm.refresh(true);

        // start up the update executor to process update events from the remote config servers.
        lcm.startUpdateExecutor();
    }


    private void initializeRemoteConfigServer(){
        Set<RemoteRetriever> remoteRetrievers = lookForRemoteServerBySpi();
        logger.info("There are {} remote config providers found. Details: >>>>>>>>> ", remoteRetrievers.size());
        for(RemoteRetriever retriever :remoteRetrievers){
            logger.info("{} remote config provider is found.", retriever.getName());
            lcm.addRemoteRetriever(retriever);
        }
        logger.info("Remote config providers are loaded successfully. <<<<<<<<<< ", remoteRetrievers.size());
    }

    private Set<RemoteRetriever> lookForRemoteServerBySpi(){

        Set<RemoteRetriever> providers = new TreeSet<RemoteRetriever>(new Comparator<RemoteRetriever>(){
            @Override
            public int compare(RemoteRetriever o1, RemoteRetriever o2) {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });

        for (final ClassLoader classLoader : getClassLoaders()) {
            try {
                for (final RemoteRetriever provider : ServiceLoader.load(RemoteRetriever.class, classLoader)) {
                    providers.add(provider);
                }
            } catch (final Throwable ex) {
                logger.warn("There is something wrong occurred in provider lookup step. Details: {}", ex.getMessage());
            }
        }

        return providers;

    }

    public ClassLoader[] getClassLoaders() {
        final Collection<ClassLoader> classLoaders = new LinkedHashSet<>();
        classLoaders.add(getClass().getClassLoader());
        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader != null) {
            classLoaders.add(systemClassLoader);
        }
        return classLoaders.toArray(new ClassLoader[classLoaders.size()]);
    }


}
