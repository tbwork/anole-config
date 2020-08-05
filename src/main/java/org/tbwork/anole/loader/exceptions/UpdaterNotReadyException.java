package org.tbwork.anole.loader.exceptions;
 
 
public class UpdaterNotReadyException extends RuntimeException {

	private static String errorMessage = "The updater has not been initialized, please initialize it first.";

	public UpdaterNotReadyException()
    {
    	super(errorMessage);
    }
	 
}
