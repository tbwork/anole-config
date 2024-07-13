package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;

@AnoleConfigLocation(locations="*.anole")
public class TestAnole {

	private static final AnoleLogger logger = new AnoleLogger(TestAnole.class);

	public static void main(String[] args) throws IOException, InterruptedException {

		AnoleApp.start(LogLevel.INFO);

		System.out.println(Anole.getProperty("b"));
		System.out.println(Anole.getProperty("abcdef"));

	}
}
