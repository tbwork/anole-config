package com.github.tbwork.anole.loader.core.manager.monitor.processor;

/**
 *  Anole hook that allows for custom further process of a config change.
 */
public interface AnoleOutgoConfigUpdatePostProcessor {


    /**
     *  Called when new change of config arrived.
     *
     * @param key the processed key.
     * @param newValue the new value.
     * @param updateTime the update time.
     */
    void process(String key, String newValue, long updateTime);


}
