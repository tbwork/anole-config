package org.tbwork.anole.loader;

import org.tbwork.anole.loader.annotion.AnoleClassPathFilter;
import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;

@AnoleConfigLocation(locations="*.anole")
@AnoleClassPathFilter(contains = {"anole-*"})
public class TestAnole {
	public static void main(String[] args) throws IOException {
		AnoleApp.setEnvironmentFromClassPathFile("env.anole");
		AnoleApp.setEnvironment("local");
		AnoleApp.start( LogLevel.DEBUG);
		System.out.println(Anole.getProperty("a"));
	}
}
