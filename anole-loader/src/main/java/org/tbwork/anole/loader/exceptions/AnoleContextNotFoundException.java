package org.tbwork.anole.loader.exceptions;
 
 
public class AnoleContextNotFoundException extends RuntimeException {

	private static String errorMessage = "There is no Anole context found, please create one first. See AnoleClasspathConfigContext.";

	public AnoleContextNotFoundException()
    {
    	super(errorMessage);
    }
	 
}
