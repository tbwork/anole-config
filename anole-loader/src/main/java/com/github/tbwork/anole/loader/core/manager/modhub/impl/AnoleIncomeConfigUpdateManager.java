package com.github.tbwork.anole.loader.core.manager.modhub.impl;

import com.github.tbwork.anole.loader.core.manager.monitor.impl.IncomeAnoleConfigUpdateMonitor;
import com.lmax.disruptor.*;
import com.github.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;

/**
 * <p>Manage all income update events from outer triggers, receive them and then process them.</p>
 */
public class AnoleIncomeConfigUpdateManager extends AbstractConfigUpdateManager {


    @Override
    protected Integer getQueueSize() {
        return 1024;
    }

    @Override
    protected EventHandler getEventHandler() {
        return new IncomeAnoleConfigUpdateMonitor(AnoleConfigManager.getInstance());
    }

    @Override
    protected String getThreadPrefix() {
        return "anole-income-update";
    }
}
