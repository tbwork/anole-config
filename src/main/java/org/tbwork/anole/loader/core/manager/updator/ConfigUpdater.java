package org.tbwork.anole.loader.core.manager.updator;

import org.tbwork.anole.loader.core.model.UpdateEvent;

/**
 * Retrieve the config updating event and process it.
 */
public interface ConfigUpdater {



    /**
     * Tell the updater to start.
     */
    public void start();

    /**
     * Publish a config update.
     * @param updateEvent
     */
    public void publishEvent(UpdateEvent updateEvent);




}
