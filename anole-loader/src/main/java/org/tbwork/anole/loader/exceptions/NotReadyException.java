package org.tbwork.anole.loader.exceptions;
 
 
public class NotReadyException extends RuntimeException {

	private static String errorMessage = "The %s has not been initialized, please initialize it first.";

	public NotReadyException(String notReadyTarget)
    {
    	super( String.format(errorMessage, notReadyTarget));
    }
	 
}
