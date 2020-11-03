package org.tbwork.anole.loader.statics;

/**
 * Built-in config key books.
 */
public interface BuiltInConfigKeyBook {

    /**
     * <p>Whether force to refresh config's referencing key's config.</p>
     * <p>E.g.,</p>
     * <pre>
     *     a = ${b}_hello
     * </pre>
     * <p> The b's value would be also re-resolved for that it is referenced by config 'a'.</p>
     * <p><b>Type:</b> Boolean</p>
     */
    String FORCE_REFRESH_REFERENCING_KEYS = "anole.config.core.force_refresh_referencing_keys";

    /**
     * <p>Anole running mode:</p>
     * <p> "strict" means every configuration should be parsed successfully
     * at the end, </p>
     * <p>otherwise not.</p>
     */
    String ANOLE_MODE_KEY = "anole.mode";


    /**
     * <p>Tell anole whether or not to clean or system properties loaded
     * from anole after the initialization.</p>
     */
    String CLEAN_SYSTEM_PROPERTY_AFTER_INITIALIZATION = "anole.system.property.clean";

}
