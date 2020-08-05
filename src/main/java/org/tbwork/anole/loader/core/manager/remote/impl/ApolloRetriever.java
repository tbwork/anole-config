package org.tbwork.anole.loader.core.manager.remote.impl;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.manager.monitor.Monitor;
import org.tbwork.anole.loader.core.model.ConfigFileResource;
import org.tbwork.anole.loader.util.AnoleAssertUtil;
import org.tbwork.anole.loader.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Apollo config retriever
 */
public class ApolloRetriever extends AbstractRetriever {

    private static final Logger logger = LoggerFactory.getLogger(ApolloRetriever.class);

    private List<Config> configList;


    @Override
    public String retrieve(String key) {

        for(Config config : configList){
           String tempValue = config.getProperty(key, null);
           if(StringUtil.isNotEmpty(tempValue)){
               return tempValue;
           }
        }
        return null;
    }

    @Override
    public void registerMonitor(final Monitor monitor) {
        for(Config config : configList){
            config.addChangeListener(new ConfigChangeListener() {
                @Override
                public void onChange(ConfigChangeEvent changeEvent) {
                    for(String changedKey : changeEvent.changedKeys()){
                        monitor.monitorChange( changedKey,
                                changeEvent.getChange(changedKey).getNewValue(),
                                System.currentTimeMillis()
                        );
                    }
                }
            });
        }
    }

    @Override
    public void setRemoteEnvironment(String environment) {
         Anole.setProperty("env", environment);
    }

    @Override
    public String getName() {
        return "Apollo";
    }


    @Override
    protected void checkReady() {

        AnoleAssertUtil.assertBasicConfigDefined("apollo.namespaces", "application");
        AnoleAssertUtil.assertBasicConfigDefined("apollo.meta", "http://localhost:8080");
        AnoleAssertUtil.assertBasicConfigDefined("apollo.cluster", "dev-cluster-1");

    }

    @Override
    protected void initialize() {
        configList = new ArrayList<>();
        String [] namespaces = StringUtil.trimStrings(Anole.getRawValue("apollo.namespaces").trim().split(","));
        for(String namespace : namespaces){
            configList.add( ConfigService.getConfig(namespace));
        }
    }
}
