package org.tbwork.anole.loader.util;

import org.tbwork.anole.loader.Anole;
import org.tbwork.anole.loader.AnoleApp;
import org.tbwork.anole.loader.exceptions.BasicConfigMustBeSpecifiedClearly;
import org.tbwork.anole.loader.exceptions.ConfigNotSetException;

public class AnoleAssertUtil {

    private static final AnoleLogger logger = new AnoleLogger(AnoleApp.class);

    /**
     * Assert the config named key is already set, otherwise throw an exception.
     * @param key the target key
     */
    public static void assertConfigPresent(String key){
        if(!Anole.isPresent(key)){
            logAndThrow( new ConfigNotSetException(key) );
        }
    }

    /**
     * Assert the key is already defined, or throw en exception with an example
     * in right format.
     * @param key the given key
     * @param example the example in right format
     */
    public static void assertBasicConfigDefined(String key, String example){
        assertConfigPresent(key);
        if(AnoleValueUtil.containVariable(Anole.getRawValue(key))){
            logAndThrow( new BasicConfigMustBeSpecifiedClearly(key, example) );
        }
    }


    /**
     * Assert the key is already defined, or throw en exception.
     * @param key the given key
     */
    public static void assertBasicConfigDefined(String key){
        assertConfigPresent(key);
        if(AnoleValueUtil.containVariable(Anole.getRawValue(key))){
            logAndThrow(new BasicConfigMustBeSpecifiedClearly(key));
        }
    }

    private static void logAndThrow(RuntimeException e){
        logger.error(e.getMessage());
        throw e;
    }

}
