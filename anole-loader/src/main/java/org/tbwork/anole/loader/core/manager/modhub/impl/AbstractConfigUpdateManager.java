package org.tbwork.anole.loader.core.manager.modhub.impl;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.tbwork.anole.loader.core.manager.modhub.ConfigUpdateManager;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.core.model.UpdateEventFactory;
import org.tbwork.anole.loader.exceptions.UpdaterNotReadyException;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractConfigUpdateManager implements ConfigUpdateManager {

    private Disruptor<UpdateEvent> disruptor = null;

    private volatile boolean startRecording = false;


    @Override
    public void startRecord() {
        EventFactory<UpdateEvent> eventEventFactory = new UpdateEventFactory();
        String threadPrefix = getThreadPrefix();
        ThreadFactory threadFactory =  new AnoleUpdaterThreadFactory(threadPrefix);
        int queueSize = getQueueSize();
        disruptor = new Disruptor<UpdateEvent>(eventEventFactory,
                queueSize,
                threadFactory,
                ProducerType.MULTI,// multiple publisher
                new BlockingWaitStrategy());
        disruptor.handleEventsWith(getEventHandler());
        startRecording = true;
    }

    @Override
    public void startProcess() {
        if (!startRecording) {
            throw new UpdaterNotReadyException();
        }
        disruptor.start();
    }

    @Override
    public void shutDown() {
        if (!startRecording) {
            throw new UpdaterNotReadyException();
        }
        disruptor.shutdown();
    }

    @Override
    public void publishEvent(final UpdateEvent updateEvent) {

        if (!startRecording) {
            throw new UpdaterNotReadyException();
        }

        disruptor.publishEvent(new EventTranslator<UpdateEvent>() {
            @Override
            public void translateTo(UpdateEvent event, long sequence) {
                event.setCreateTime(updateEvent.getCreateTime());
                event.setKey(updateEvent.getKey());
                event.setNewValue(updateEvent.getNewValue());
            }
        });

    }

    protected abstract Integer getQueueSize();

    protected abstract EventHandler getEventHandler();

    protected abstract String getThreadPrefix();


    /**
     * The default thread factory
     */
    public static class AnoleUpdaterThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        AnoleUpdaterThreadFactory(String prefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = prefix +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}