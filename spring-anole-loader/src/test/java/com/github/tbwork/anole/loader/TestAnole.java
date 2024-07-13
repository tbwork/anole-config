package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.loader.util.AnoleLogger;

import java.io.IOException;

@AnoleConfigLocation(locations="*.anole")
public class TestAnole {

	private static final AnoleLogger logger = new AnoleLogger(TestAnole.class);

	public static void main(String[] args) throws IOException, InterruptedException {

		Anole.setProperty("num", "123");

		AnoleApp.start(AnoleLogger.LogLevel.INFO);

		logger.info(Anole.getProperty("test"));

	}
}
