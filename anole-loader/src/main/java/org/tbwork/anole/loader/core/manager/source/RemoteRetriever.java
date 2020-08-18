package org.tbwork.anole.loader.core.manager.source;

import org.tbwork.anole.loader.core.manager.monitor.RemoteMonitor;

/**
 * <p>
 * A RemoteRetriever is a special SourceRetriever, which not only could lookup and
 * retrieve config values from the remote config source, but also could monitor the
 * config change.
 * </p>
 * <p>
 * Remote config environment is separated from Anole's local environment, which means
 * the remote config environment is separated from application's runtime environment.
 * </p>
 * <p>
 *  E.g., there are "dev", "test", "product" three environments in remote config server, but
 *  there are "dev", "local", "test", "pre", "stage", "gray", "online" seven runtime environments.
 * </p>
 * <p>
 * It is very convenient to load configs from different remote environments under the same
 * local environment.
 * </p>
 */
public interface RemoteRetriever extends SourceRetriever{


    /**
     * Register a monitor to be aware of configuration change.
     * @param remoteMonitor
     */
    void registerMonitor(RemoteMonitor remoteMonitor);

    /**
     * Tell Anole to load configs from which environment of the remote config server.
     * @param environment the target environment
     */
    void setRemoteEnvironment(String environment);


}
