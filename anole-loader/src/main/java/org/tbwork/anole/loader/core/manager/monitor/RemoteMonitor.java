package org.tbwork.anole.loader.core.manager.monitor;

/**
 * 监听者
 */
public interface RemoteMonitor {


    /**
     *
     * Monitor any update of configs.
     *
     * @param key  the config key
     * @param destValue the new value
     * @param occurTime the time of update occurrence.
     */
    void monitorChange(String key, String destValue, long occurTime);


}
