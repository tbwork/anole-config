package com.github.tbwork.anole.log4j;

import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(name = "a", category = StrLookup.CATEGORY)
public class AnoleShortPrefixLookup extends AnoleLookup{
}
