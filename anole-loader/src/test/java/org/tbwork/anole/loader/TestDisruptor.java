package org.tbwork.anole.loader;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.updater.impl.AnoleConfigUpdateManager;
import org.tbwork.anole.loader.core.model.UpdateEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestDisruptor {



    /**
     * Event handler used to process update events.
     */
    public static class  UpdateEventHandler implements EventHandler<UpdateEvent> {

        private ConfigManager anoleConfigManager;


        @Override
        public void onEvent(UpdateEvent event, long sequence, boolean endOfBatch) throws Exception {

            System.out.println("event is processed!!!");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        EventFactory<UpdateEvent> eventEventFactory = new AnoleConfigUpdateManager.UpdateEventFactory();
        Disruptor<UpdateEvent> disruptor = new Disruptor<UpdateEvent>(eventEventFactory,
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.MULTI,// multiple publisher
                new BlockingWaitStrategy());
        disruptor.handleEventsWith(new UpdateEventHandler());

        disruptor.publishEvent(new EventTranslator<UpdateEvent>() {
            @Override
            public void translateTo(UpdateEvent event, long sequence) {
                    event.setNewValue("12312312");
            }
        });

        disruptor.publishEvent(new EventTranslator<UpdateEvent>() {
            @Override
            public void translateTo(UpdateEvent event, long sequence) {
                event.setNewValue("12312312");
            }
        });

        disruptor.publishEvent(new EventTranslator<UpdateEvent>() {
            @Override
            public void translateTo(UpdateEvent event, long sequence) {
                event.setNewValue("12312312");
            }
        });

        TimeUnit.SECONDS.sleep(3L);
        disruptor.start();

    }
}
