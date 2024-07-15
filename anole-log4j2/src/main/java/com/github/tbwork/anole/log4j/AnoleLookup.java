package com.github.tbwork.anole.log4j;

import com.github.tbwork.anole.loader.Anole;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.util.Map;

@Plugin(name = "anole", category = StrLookup.CATEGORY)
public class AnoleLookup implements StrLookup {

    public AnoleLookup() {
    }

    @Override
    public String lookup(String key) {
        return Anole.getProperty(key);
    }

    @Override
    public String lookup(LogEvent event, String key) {
        return Anole.getProperty(key);
    }
}