package com.github.tbwork.anole.loader.core.manager.monitor.impl;

import com.github.tbwork.anole.loader.core.model.ConfigItem;
import com.github.tbwork.anole.loader.core.model.UpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.tbwork.anole.loader.core.manager.ConfigManager;
import com.github.tbwork.anole.loader.core.manager.monitor.AnoleConfigMonitor;

/**
 *  Monitor the changes from out triggers applied to Anole's config items.
 */
public class IncomeAnoleConfigUpdateMonitor implements AnoleConfigMonitor {

    private static final Logger logger = LoggerFactory.getLogger(IncomeAnoleConfigUpdateMonitor.class);

    private ConfigManager anoleConfigManager;

    public IncomeAnoleConfigUpdateMonitor(ConfigManager anoleConfigManager){
        this.anoleConfigManager = anoleConfigManager;
    }


    @Override
    public void onEvent(UpdateEvent event, long sequence, boolean endOfBatch) {

        try {
            processEvent(event);
        }
        catch (Throwable throwable){
            logger.error("[IMPORTANT!!!] Error occurs while processing event, details: {}", throwable.getMessage());
        }

    }


    private void processEvent(UpdateEvent event){
        ConfigItem configItem =  anoleConfigManager.getConfigItem(event.getKey());

        if(configItem == null){
            configItem = anoleConfigManager.registerAndSetValue(event.getKey(), event.getNewValue(), event.getCreateTime());
        }

        String oldDefinition = configItem.getDefinition();

        if(oldDefinition == null && event.getNewValue() == null){
            return ;
        }

        if(oldDefinition != null && oldDefinition.equals(event.getNewValue())){
            return ;
        }

        if(configItem.getLastUpdateTime() > event.getCreateTime()){
            return ; // ignore old version update
        }

        anoleConfigManager.registerAndSetValue(event.getKey(), event.getNewValue(), event.getCreateTime());

        logger.info("The key named '{}' changed from '{}' to '{}'", event.getKey(), oldDefinition, event.getNewValue());
    }
}