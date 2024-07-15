package com.github.tbwork.anole.log4j;

import static org.junit.Assert.assertTrue;

import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.AnoleApp;
import com.github.tbwork.anole.loader.context.AnoleContext;
import com.github.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.test.AnoleTest;
import junit.framework.JUnit4TestAdapter;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
@AnoleTest
public class AppTest
{
    @Test
    public void shouldAnswerWithTrue()
    {
        Logger logger = LoggerFactory.getLogger(AppTest.class);
        logger.info( "anole-log4j2 passes all tests!" );
    }
}
