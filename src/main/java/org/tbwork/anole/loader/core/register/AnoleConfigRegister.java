package org.tbwork.anole.loader.core.register;

import org.tbwork.anole.loader.core.manager.impl.LocalConfigManager;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.util.SingletonFactory;

import java.util.List;

public class AnoleConfigRegister {

    private static final LocalConfigManager lcm = SingletonFactory.getLocalConfigManager();


    /**
     * Register all KVs to the config manager center.
     * @param rawKVList
     */
    public void register(List<RawKV> rawKVList){

    }


}
