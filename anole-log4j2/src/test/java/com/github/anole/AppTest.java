package com.github.anole;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        PluginManager.addPackage("org.tbwork.anole.log4j");
        Logger logger = LoggerFactory.getLogger(App.class);
        logger.info( "Hello World!" );
    }
}
