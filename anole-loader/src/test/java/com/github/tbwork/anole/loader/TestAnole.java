package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.annotion.AnoleConfigLocation;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.AnoleLogger.LogLevel;

import java.io.IOException;

@AnoleConfigLocation(locations={"*.anole"})
public class TestAnole {

	public static void main(String[] args) {

		AnoleApp.start(LogLevel.INFO);

	}
}
