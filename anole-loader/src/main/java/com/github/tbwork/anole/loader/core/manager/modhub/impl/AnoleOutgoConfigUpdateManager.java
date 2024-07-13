package com.github.tbwork.anole.loader.core.manager.modhub.impl;

import com.github.tbwork.anole.loader.core.manager.monitor.impl.OutgoAnoleConfigUpdateMonitor;
import com.lmax.disruptor.EventHandler;

/**
 * <p> Manage all changes of Anole config item submitted by Anole itself.</p>
 */
public class AnoleOutgoConfigUpdateManager extends AbstractConfigUpdateManager{

    @Override
    protected Integer getQueueSize() {
        return 1024;
    }

    @Override
    protected EventHandler getEventHandler() {
        return new OutgoAnoleConfigUpdateMonitor();
    }

    @Override
    protected String getThreadPrefix() {
        return "anole-outgo-update";
    }
}