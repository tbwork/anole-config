package org.tbwork.anole.loader.core.manager.monitor.impl;

import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.monitor.Monitor;
import org.tbwork.anole.loader.core.manager.updator.ConfigUpdater;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.util.AnoleLogger;

/**
 * Config change monitor.
 */
public class ConfigChangeMonitor implements Monitor {

    private static final AnoleLogger logger = new AnoleLogger(ConfigChangeMonitor.class);

    private ConfigUpdater configUpdater;

    private ConfigManager configManager;

    public ConfigChangeMonitor(ConfigUpdater configUpdater, ConfigManager configManager){
        this.configUpdater = configUpdater;
        this.configManager = configManager;
    }

    @Override
    public void monitorChange(String key, String destValue, long occurTime) {
        if(configManager.interest(key)){
            configUpdater.publishEvent(new UpdateEvent(key, destValue, occurTime));
        }
        else{
            logger.debug("One update is ignored because the current application is not interested in this key ({})", key);
        }
    }

}
