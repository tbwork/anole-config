package com.github.tbwork.anole.log4j;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.util.HashMap;
import java.util.Map;

@Plugin(name = "properties", category = Node.CATEGORY, printObject = true)
public final class AnolePropertiesPlugin {

    private AnolePropertiesPlugin() {
    }

    /**
     * Creates the Properties component.
     * @param properties An array of Property elements.
     * @param config The Configuration.
     * @return An Interpolator that includes the configuration properties.
     */
    @PluginFactory
    public static StrLookup configureSubstitutor(@PluginElement("Properties") final Property[] properties,
                                                 @PluginConfiguration final Configuration config) {
        return new Interpolator(new AnoleLookup(), config.getPluginPackages());
    }
}

