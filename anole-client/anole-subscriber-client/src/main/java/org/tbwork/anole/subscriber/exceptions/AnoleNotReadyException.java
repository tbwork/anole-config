package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.loader.types.ConfigType;
 
public class AnoleNotReadyException extends RuntimeException {

	private static String errorMessage = "The anole has not been initialized, please initialize it first.";
	
	public AnoleNotReadyException()
    {
    	super(errorMessage);
    }
	 
}
