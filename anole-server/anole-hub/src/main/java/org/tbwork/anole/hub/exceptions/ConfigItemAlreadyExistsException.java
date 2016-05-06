package org.tbwork.anole.hub.exceptions;

import org.tbwork.anole.common.ConfigType;
 
public class ConfigItemAlreadyExistsException extends RuntimeException {

	private static final String errorMessage = "The configuration (key = %s) is already existed, please modify it if you want to set new value for it.";
	
	public ConfigItemAlreadyExistsException()
    {
    	super(String.format(errorMessage,"unknown"));
    }
	
	public ConfigItemAlreadyExistsException(String key)
    {
    	super(String.format(errorMessage, key));
    }
	 
}
