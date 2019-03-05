package org.tbwork.anole.loader.exceptions;
 
public class BadFileException extends RuntimeException {

	public BadFileException()
    {
		super("Could not open the file.");
    }

	public BadFileException(String filepath)
    {
		super(String.format("Could not open the file: %s", filepath));
    }
}
