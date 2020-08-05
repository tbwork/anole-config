package org.tbwork.anole.loader.util;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.exceptions.BasicConfigMustBeSpecifiedClearly;
import org.tbwork.anole.loader.exceptions.ConfigNotSetException;

public class AnoleAssertUtil {

    /**
     * Assert the config named key is already set, otherwise throw an exception.
     * @param key the target key
     */
    public static void assertConfigPresent(String key){
        if(!Anole.isPresent(key)){
            throw new ConfigNotSetException(key);
        }
    }

    public static void assertBasicConfigDefined(String key, String example){
        assertConfigPresent(key);
        if(AnoleValueUtil.containVariable(Anole.getRawValue(key), key)){
            throw new BasicConfigMustBeSpecifiedClearly(key, example);
        }
    }

}
