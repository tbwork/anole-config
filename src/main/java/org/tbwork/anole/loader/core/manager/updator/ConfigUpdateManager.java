package org.tbwork.anole.loader.core.manager.updator;

import org.tbwork.anole.loader.core.model.UpdateEvent;

/**
 * Retrieve the config updating event and process it.
 */
public interface ConfigUpdateManager {


    /**
     * Tell the updater to start recording update events.
     */
    public void startRecord();


    /**
     * Tell the updater to start process update events.
     */
    public void startProcess();

    /**
     * Shut down the updater.
     */
    public void shutDown();


    /**
     * Publish a config update.
     * @param updateEvent
     */
    public void publishEvent(UpdateEvent updateEvent);




}
