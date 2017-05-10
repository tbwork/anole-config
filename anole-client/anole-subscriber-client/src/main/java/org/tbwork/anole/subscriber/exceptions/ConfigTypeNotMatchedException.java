package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.loader.types.ConfigType;
 
public class ConfigTypeNotMatchedException extends RuntimeException {
 
	public ConfigTypeNotMatchedException()
    {
		super("The type of config does not match the target type user request.");
    }
	
    public ConfigTypeNotMatchedException(ConfigType valueType, ConfigType targetType)
    {
    	super(String.format( "The type of config (%s) does not match the target type (%s) user request.", valueType, targetType));
    }
	
}
