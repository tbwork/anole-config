package org.tbwork.anole.loader.core.model;

import com.lmax.disruptor.EventFactory;

public class UpdateEventFactory implements EventFactory<UpdateEvent> {
    @Override
    public UpdateEvent newInstance() {
        return new UpdateEvent(null, null, 0);
    }
}