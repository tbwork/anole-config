package com.github.tbwork.anole.loader.exceptions;
 
 
public class AnoleNotReadyException extends RuntimeException {

	private static String errorMessage = "The anole has not been initialized, so the '%s' is not available now, please initialize it first.";
	
	public AnoleNotReadyException(String key)
    {
    	super(String.format(errorMessage, key));
    }
	 
}
