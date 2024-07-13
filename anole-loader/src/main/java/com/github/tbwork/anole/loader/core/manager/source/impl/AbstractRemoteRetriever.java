package com.github.tbwork.anole.loader.core.manager.source.impl;

import com.github.tbwork.anole.loader.util.AnoleAssertUtil;
import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.core.manager.source.RemoteRetriever;

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
