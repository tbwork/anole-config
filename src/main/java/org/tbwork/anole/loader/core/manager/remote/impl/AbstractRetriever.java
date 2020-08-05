package org.tbwork.anole.loader.core.manager.remote.impl;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.manager.remote.RemoteRetriever;
import org.tbwork.anole.loader.util.AnoleAssertUtil;

public abstract class AbstractRetriever implements RemoteRetriever {

    public AbstractRetriever(){
        AnoleAssertUtil.assertBasicConfigDefined("remote.env", "DEV");
        setRemoteEnvironment(Anole.getRawValue("remote.env"));
        checkReady();
        initialize();
    }

    /**
     * Check whether the related properties are defined or not.
     */
    protected abstract void checkReady();

    /**
     * Do the initialization.
     */
    protected abstract void initialize();
}
