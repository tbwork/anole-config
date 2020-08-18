package org.tbwork.anole.loader.core.manager.monitor;

import com.lmax.disruptor.EventHandler;
import org.tbwork.anole.loader.core.model.UpdateEvent;

/**
 * Anole monitor is used to monitor any operation applied to the config items.
 */
public interface AnoleConfigMonitor extends EventHandler<UpdateEvent> {

}
