package com.github.tbwork.anole.loader.core.register;

import com.github.tbwork.anole.loader.spiext.ConfigSource;
import com.github.tbwork.anole.loader.core.model.RawKV;
import com.github.tbwork.anole.loader.statics.BuiltInConfigKeys;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.ProjectUtil;
import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.core.manager.ConfigManager;
import com.github.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;

import java.util.*;

public class AnoleConfigRegister {

    private static final AnoleLogger logger = new AnoleLogger(AnoleConfigRegister.class);

    private ConfigManager lcm;


    /**
     * Register all KVs to the config manager center.
     * @param rawKVList
     */
    public void register(List<RawKV> rawKVList){

        lcm = AnoleConfigManager.getInstance();

        // register raw definition
        lcm.batchRegisterDefinition(rawKVList);

        // refresh configs locally
        lcm.refresh(false);

        // register to system for other framework to read.
        lcm.registerToSystem();

        // refresh all properties
        if(Anole.getBoolProperty(BuiltInConfigKeys.ANOLE_STRICT_MODE, false)){
            lcm.refresh(true);
        }
        else{
            lcm.refresh(false);
        }

        // remove anole configurations from system
        if("true".equals(Anole.getRawValue(BuiltInConfigKeys.CLEAN_SYSTEM_PROPERTY_AFTER_INITIALIZATION))){
            lcm.removeFromSystem();
        }
    }


}
