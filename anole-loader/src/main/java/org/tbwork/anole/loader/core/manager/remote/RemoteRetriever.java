package org.tbwork.anole.loader.core.manager.remote;

import lombok.Data;
import org.tbwork.anole.loader.core.manager.monitor.Monitor;

public interface RemoteRetriever {

    /**
     * Retrieve the value of specified target key.
     * @param key the target key
     * @return the value of the target key.
     */
    String retrieve(String key);

    /**
     * Register a monitor to be aware of configuration change.
     * @param monitor
     */
    void registerMonitor(Monitor monitor);

    /**
     * Tell Anole to load configs from which environment of the remote config server.
     * @param environment the target environment
     */
    void setRemoteEnvironment(String environment);

    /**
     * Get the retriever's name
     * @return
     */
    String getName();

}
