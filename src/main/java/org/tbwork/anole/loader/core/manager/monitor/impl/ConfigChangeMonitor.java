package org.tbwork.anole.loader.core.manager.monitor.impl;

import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.monitor.Monitor;
import org.tbwork.anole.loader.core.manager.updator.ConfigUpdater;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.util.AnoleLogger;

/**
 * Config change monitor.
 */
public class ConfigChangeMonitor implements Monitor {

    private static final AnoleLogger logger = new AnoleLogger(ConfigChangeMonitor.class);

    private ConfigUpdater configUpdater;

    public ConfigChangeMonitor(ConfigUpdater configUpdater){
        this.configUpdater = configUpdater;
    }

    @Override
    public void monitorChange(String key, String destValue, long occurTime) {
        configUpdater.publishEvent(new UpdateEvent(key, destValue, occurTime));
    }

}
