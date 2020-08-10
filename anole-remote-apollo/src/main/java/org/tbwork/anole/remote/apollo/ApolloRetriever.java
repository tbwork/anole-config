package org.tbwork.anole.remote.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.manager.monitor.Monitor;
import org.tbwork.anole.loader.core.manager.remote.impl.AbstractRetriever;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Apollo config retriever
 */
public class ApolloRetriever extends AbstractRetriever {

    private static final AnoleLogger logger = new AnoleLogger(ApolloRetriever.class);

    private List<Config> configList;

    public ApolloRetriever(){
        super("app.id", "apollo.cluster", "apollo.meta", "apollo.namespaces");
    }

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
         System.setProperty("env", environment);
    }

    @Override
    public String getName() {
        return "Apollo";
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
