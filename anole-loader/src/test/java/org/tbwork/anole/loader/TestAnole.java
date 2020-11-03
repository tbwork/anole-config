package org.tbwork.anole.loader;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;

@AnoleConfigLocation(locations="*.anole")
public class TestAnole {

	private static final AnoleLogger logger = new AnoleLogger(TestAnole.class);

	public static void main(String[] args) throws IOException, InterruptedException {

		AnoleApp.start(LogLevel.INFO);

		System.out.println(Anole.getProperty("b"));

	}
}
