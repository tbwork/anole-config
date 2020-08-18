package org.tbwork.anole.loader.core.manager.source.impl;

import org.tbwork.anole.loader.Anole;
import org.tbwork.anole.loader.core.manager.source.RemoteRetriever;
import org.tbwork.anole.loader.util.AnoleAssertUtil;

/**
 * Abstract implement of RemoteRetriever
 */
public abstract class AbstractRemoteRetriever extends AbstractRetriever implements RemoteRetriever {

    public AbstractRemoteRetriever(){
        super();
    }


    /**
     * Check whether the required properties are defined or not.
     */
    @Override
    protected void checkAllRequiredProperties(){
       super.checkAllRequiredProperties();
       AnoleAssertUtil.assertBasicConfigDefined("remote.env", "DEV");
    }

    @Override
    protected void initialize() {
        setRemoteEnvironment(Anole.getRawValue("remote.env"));
        doInitialization();
    }

    /**
     * Do the initialization.
     */
    protected abstract void doInitialization();
}
