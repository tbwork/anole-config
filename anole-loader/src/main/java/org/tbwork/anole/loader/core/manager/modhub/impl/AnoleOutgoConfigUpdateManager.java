package org.tbwork.anole.loader.core.manager.modhub.impl;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.tbwork.anole.loader.core.manager.modhub.ConfigUpdateManager;
import org.tbwork.anole.loader.core.manager.monitor.impl.OutgoAnoleConfigUpdateMonitor;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.core.model.UpdateEventFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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