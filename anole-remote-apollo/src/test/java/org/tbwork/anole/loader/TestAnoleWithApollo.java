package org.tbwork.anole.loader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@AnoleConfigLocation(locations="*.anole")
public class TestAnoleWithApollo {

	private static final AnoleLogger logger = new AnoleLogger(TestAnoleWithApollo.class);

	@Test
	public void getConfigFromApollo() throws IOException, InterruptedException {

		Anole.setProperty("num", "123");

		AnoleApp.start(LogLevel.INFO);

		logger.info(Anole.getProperty("test"));

	}
}
