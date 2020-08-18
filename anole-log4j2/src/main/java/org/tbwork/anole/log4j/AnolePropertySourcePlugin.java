package org.tbwork.anole.log4j;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.tbwork.anole.loader.Anole;

/**
 * Used for log4j 2.x. Define log4j properties like ${a:key}
 * @since 1.2.8
 */
@Plugin(name="a", category = StrLookup.CATEGORY)
public class AnolePropertySourcePlugin implements StrLookup {
    @Override
    public String lookup(String key) {
        return Anole.getProperty(key);
    }

    @Override
    public String lookup(LogEvent event, String key) {
        return Anole.getProperty(key);
    }
}
