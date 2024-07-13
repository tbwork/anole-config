package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.core.manager.ConfigManager;
import com.github.tbwork.anole.loader.core.model.UpdateEvent;
import com.lmax.disruptor.EventHandler;

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

}
