package org.tbwork.anole.loader.core.manager.modhub.impl;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.loader.core.manager.modhub.ConfigUpdateManager;
import org.tbwork.anole.loader.core.manager.monitor.impl.IncomeAnoleConfigUpdateMonitor;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.core.model.UpdateEventFactory;
import org.tbwork.anole.loader.exceptions.UpdaterNotReadyException;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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
