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

}
