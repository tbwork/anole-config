package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.junit.Test;

import java.io.IOException;

@AnoleConfigLocation(locations="*.anole")
public class TestAnoleWithApollo {

	private static final AnoleLogger logger = new AnoleLogger(TestAnoleWithApollo.class);

	@Test
	public void getConfigFromApollo() throws IOException, InterruptedException {

		Anole.setProperty("num", "123");

		PluginManager.addPackage("org.tbwork.anole.loader.Anole");

		AnoleApp.start(AnoleLogger.LogLevel.INFO);

		logger.info(Anole.getProperty("test"));

	}
}
