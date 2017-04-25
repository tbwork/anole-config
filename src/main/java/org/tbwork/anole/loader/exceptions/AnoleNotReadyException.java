package org.tbwork.anole.loader.exceptions;

import org.tbwork.anole.common.ConfigType;
 
public class AnoleNotReadyException extends RuntimeException {

	private static String errorMessage = "The anole has not been initialized, please initialize it first.";
	
	public AnoleNotReadyException()
    {
    	super(errorMessage);
    }
	 
}
