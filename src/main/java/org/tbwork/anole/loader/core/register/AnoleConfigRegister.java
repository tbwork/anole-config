package org.tbwork.anole.loader.core.register;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.loader.core.manager.remote.impl.ApolloRetriever;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.StringUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnoleConfigRegister {

    private static final AnoleLogger logger = new AnoleLogger(AnoleConfigRegister.class);

    private ConfigManager lcm;

    /**
     * Register all KVs to the config manager center.
     * @param rawKVList
     */
    public void register(List<RawKV> rawKVList){

        lcm = AnoleConfigManager.getInstance();

        // register raw definition, those raw values may be used in the following steps.
        lcm.batchRegisterDefinition(rawKVList);

        // start up the update recorder to prepare to receive update events from the remote config servers.
        lcm.startUpdateRecorder();

        // initialize retrievers
        initializeRemoteConfigServer();

        // refresh all properties
        lcm.refresh();

        // initialized successfully
        Anole.initialized = true;

        // start up the update executor to process update events from the remote config servers.
        lcm.startUpdateExecutor();

    }


    private void initializeRemoteConfigServer(){
        ConfigItem configItem = lcm.getConfigItem("config.remote.types.supported");
        if(configItem != null && StringUtil.isNotEmpty(configItem.strValue())){
            String [] supportedRetrievers = StringUtil.trimStrings(configItem.strValue().toLowerCase().trim().split(","));
            Set<String> retrieverSet = new HashSet<>();
            retrieverSet.addAll(Arrays.asList(supportedRetrievers));
            for(String retriever :retrieverSet){
                switch (retriever){
                    case "apollo":{
                        lcm.addRemoteRetriever(new ApolloRetriever());
                        break;
                    }
                    default: {
                        logger.warn("Unknown retriever named {}, it is going to be ignored.", retriever);
                    }
                }
            }
        }
    }

}
