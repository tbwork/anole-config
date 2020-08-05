package org.tbwork.anole.loader.core.manager.updator.impl;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.tbwork.anole.loader.core.manager.updator.ConfigUpdater;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.exceptions.UpdaterNotReadyException;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Process all config updates.</p>
 */
public class AnoleConfigUpdater implements ConfigUpdater {

    private Disruptor<UpdateEvent> disruptor = null;

    private volatile boolean started = false;

    private EventHandler eventHandler;

    public AnoleConfigUpdater( EventHandler eventHandler){
        this.eventHandler = eventHandler;
    }

    @Override
    public void start() {

        EventFactory<UpdateEvent> eventEventFactory = new UpdateEventFactory();
        ThreadFactory threadFactory = new AnoleUpdaterThreadFactory();
        disruptor = new Disruptor<UpdateEvent>(eventEventFactory,
                1024,
                threadFactory,
                ProducerType.MULTI,// multiple publisher
                new BlockingWaitStrategy());

        disruptor.handleEventsWith(eventHandler);
        disruptor.start();
        started = true;
    }

    @Override
    public void publishEvent(final UpdateEvent updateEvent) {

        if(!started){
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





    public static class UpdateEventFactory implements EventFactory<UpdateEvent> {
        @Override
        public UpdateEvent newInstance() {
            return new UpdateEvent();
        }
    }



    /**
     * The default thread factory
     */
    public static class AnoleUpdaterThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        AnoleUpdaterThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "anole-updater-" +
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
