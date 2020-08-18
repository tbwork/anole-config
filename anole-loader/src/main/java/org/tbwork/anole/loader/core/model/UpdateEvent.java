package org.tbwork.anole.loader.core.model;


import lombok.Data;

/**
 * Update events of configs. One update event could be submitted from outer triggers,
 * or proposed by Anole itself.
 */
@Data
public class UpdateEvent {

    private String key;

    private String newValue;

    private long createTime;

    /**
     * @param key the key
     * @param newValue new value
     * @param occurTime the update's occurrence time.
     */
    public UpdateEvent(String key, String newValue, long occurTime){
        this.key = key;
        this.newValue = newValue;
        this.createTime = occurTime;
    }

    /**
     * @param key the key
     * @param newValue new value
     */
    public UpdateEvent(String key, String newValue ){
        this.key = key;
        this.newValue = newValue;
        this.createTime = System.currentTimeMillis();
    }
}
