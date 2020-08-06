package org.tbwork.anole.loader;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@AnoleConfigLocation(locations="*.anole")
public class TestAnole {
	public static void main(String[] args) throws IOException, InterruptedException {
		Anole.setProperty("num", "123");

		AnoleApp.start(LogLevel.INFO);

		System.out.println(Anole.getProperty("test"));
	}
}
