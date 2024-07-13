package com.github.tbwork.anole.loader.core.manager.source.impl;

import com.github.tbwork.anole.loader.util.AnoleAssertUtil;
import com.github.tbwork.anole.loader.core.manager.source.SourceRetriever;

/**
 * Abstract implement of SourceRetriever
 */
public abstract class AbstractRetriever implements SourceRetriever {


    public AbstractRetriever(){
        checkAllRequiredProperties();
        registerAllRequiredProperties();
        initialize();
    }

    protected abstract String [] getRequiredProperties();


    /**
     * Check whether the required properties are defined or not.
     */
    protected void checkAllRequiredProperties(){
        for(String item : getRequiredProperties()){
            AnoleAssertUtil.assertBasicConfigDefined(item);
        }
    }

    /**
     * Register all required properties to some place the retriever can read from at
     * the start-up stage.
     */
    protected abstract void registerAllRequiredProperties();

    /**
     * Do the initialization.
     */
    protected abstract void initialize();
}
