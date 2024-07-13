package com.github.tbwork.anole.loader.exceptions;
 
 
public class AnoleNotReadyException extends RuntimeException {

	private static String errorMessage = "The anole has not been initialized, please initialize it first.";
	
	public AnoleNotReadyException()
    {
    	super(errorMessage);
    }
	 
}
