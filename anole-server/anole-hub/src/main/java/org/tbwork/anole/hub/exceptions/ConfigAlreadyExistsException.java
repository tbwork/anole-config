package org.tbwork.anole.hub.exceptions;

import org.tbwork.anole.loader.types.ConfigType;
 
public class ConfigAlreadyExistsException extends RuntimeException {

	private static final String errorMessage = "The configuration (key = %s) is already existed, please use it directly.";
	
	public ConfigAlreadyExistsException()
    {
    	super(String.format(errorMessage,"unknown"));
    }
	
	public ConfigAlreadyExistsException(String key)
    {
    	super(String.format(errorMessage, key));
    }
	 
}
