package org.tbwork.anole.loader;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;

@AnoleConfigLocation(locations="*.anole")
public class TestAnole {

	private static final AnoleLogger logger = new AnoleLogger(TestAnole.class);

	public static void main(String[] args) throws IOException, InterruptedException {

		Anole.setProperty("num", "123");

		AnoleApp.start(LogLevel.INFO);

		logger.info(Anole.getProperty("test"));

	}
}
