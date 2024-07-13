package com.github.tbwork.anole.loader.core.manager.monitor;

import com.github.tbwork.anole.loader.core.model.UpdateEvent;
import com.lmax.disruptor.EventHandler;

/**
 * Anole monitor is used to monitor any operation applied to the config items.
 */
public interface AnoleConfigMonitor extends EventHandler<UpdateEvent> {

}
