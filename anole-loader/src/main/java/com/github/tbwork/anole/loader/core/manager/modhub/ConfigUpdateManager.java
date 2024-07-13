package com.github.tbwork.anole.loader.core.manager.modhub;

import com.github.tbwork.anole.loader.core.model.UpdateEvent;

/**
 * It is like a hub for all config update events, and also offers
 * basic management.
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
