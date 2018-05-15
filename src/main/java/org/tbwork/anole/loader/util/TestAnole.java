package org.tbwork.anole.loader.util;

import java.io.IOException;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;

@AnoleConfigLocation()
public class TestAnole {

	public static void main(String[] args) throws IOException {
		System.out.println("Application classpath:"+ProjectUtil.getApplicationClasspath());
    	System.out.println("Mainclass classpath:"+ProjectUtil.getMainclassClasspath());
    	System.out.println("Program classpath:"+ProjectUtil.getProgramClasspath());
	   	AnoleApp.start(LogLevel.DEBUG) ;
	   	System.out.println(Anole.getProjectName()); 
	}
	  
}
