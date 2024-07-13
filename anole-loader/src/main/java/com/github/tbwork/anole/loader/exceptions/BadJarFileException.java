package com.github.tbwork.anole.loader.exceptions;
 
public class BadJarFileException extends RuntimeException {
 
	public BadJarFileException()
    {
		super("Could not open the jar file.");
    }
	 
	public BadJarFileException(String filepath)
    {
		super(String.format("Could not open the jar file: %s", filepath));
    }
}
