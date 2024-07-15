package com.github.tbwork.anole.loader.statics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Built-in config key books.
 */
public class BuiltInConfigKeys {

    /**
     * <p>Whether force to refresh config's referencing key's config.</p>
     * <p>E.g.,</p>
     * <pre>
     *     a = ${b}_hello
     * </pre>
     * <p> The b's value would be also re-resolved for that it is referenced by config 'a'.</p>
     * <p><b>Type:</b> Boolean</p>
     */
    public static final String FORCE_REFRESH_REFERENCING_KEYS = "anole.diffuse.when.change";

    /**
     * <p>Anole strict mode:</p>
     * <p> "true" means every configuration should be parsed successfully
     * at the end, and empty value would cause exceptions; </p>
     * <p>otherwise not.</p>
     */
    public static final String ANOLE_STRICT_MODE = "anole.strict.mode";


    /**
     * <p>Anole strict mode:</p>
     * <p> "true" means every configuration should be parsed successfully
     * at the end, and empty value would cause exceptions; </p>
     * <p>otherwise not.</p>
     */
    public static final String ANOLE_ENV = "anole.runtime.currentEnvironment";


    public static final String ANOLE_ENV_SHORT = "anole.env";

    public static final String ANOLE_ENV_SHORT_CAMEL = "anoleEnv";

    /**
     * <p>Tell anole whether or not to clean or system properties loaded
     * from anole after the initialization.</p>
     */
    public static final String CLEAN_SYSTEM_PROPERTY_AFTER_INITIALIZATION = "anole.system.property.clean";



    public static final Set<String> builtInKeys = new HashSet<>();


    static {
        builtInKeys.add(FORCE_REFRESH_REFERENCING_KEYS);
        builtInKeys.add(ANOLE_STRICT_MODE);
        builtInKeys.add(CLEAN_SYSTEM_PROPERTY_AFTER_INITIALIZATION);
        builtInKeys.add(ANOLE_ENV);
        builtInKeys.add(ANOLE_ENV_SHORT);
        builtInKeys.add(ANOLE_ENV_SHORT_CAMEL);
    }

    public static boolean isBuiltInKeys(String key){
        return builtInKeys.contains(key);
    }

}
