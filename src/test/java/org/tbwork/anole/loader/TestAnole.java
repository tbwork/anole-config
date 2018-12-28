package org.tbwork.anole.loader;

import java.io.IOException;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

@AnoleConfigLocation(locations="*.anole")
public class TestAnole {
	public static void main(String[] args) throws IOException {
		AnoleApp.start(LogLevel.WARN);
		System.out.println(AnoleApp.getProjectName());
	}
}
