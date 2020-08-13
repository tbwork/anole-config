package org.tbwork.anole.loader;

import org.junit.Test;
import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;

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
