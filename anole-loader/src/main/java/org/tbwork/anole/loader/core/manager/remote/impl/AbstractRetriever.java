package org.tbwork.anole.loader.core.manager.remote.impl;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.manager.remote.RemoteRetriever;
import org.tbwork.anole.loader.util.AnoleAssertUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRetriever implements RemoteRetriever {

    private final Set<String> sysPropertySet;


    public AbstractRetriever(String ... requiredConfigKeys){
        sysPropertySet = new HashSet<>();
        sysPropertySet.addAll(Arrays.asList(requiredConfigKeys));
        AnoleAssertUtil.assertBasicConfigDefined("remote.env", "DEV");
        setRemoteEnvironment(Anole.getRawValue("remote.env"));
        checkReady();
        preInitialize();
        initialize();
    }


    /**
     * Check whether the related properties are defined or not.
     */
    private void checkReady(){
        for(String item : sysPropertySet){
            AnoleAssertUtil.assertBasicConfigDefined(item);
        }
    }

    private void preInitialize(){
        for(String item : sysPropertySet){
           System.setProperty(item, Anole.getRawValue(item));
        }
    }

    /**
     * Do the initialization.
     */
    protected abstract void initialize();
}
