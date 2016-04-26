package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.common.ConfigType;
 
public class IllegalConfigTransformException extends RuntimeException {

	private String message;
	
	public IllegalConfigTransformException()
    {
    	this.message = "The type of config value does not match the target type specified by user.";
    }
	
    public IllegalConfigTransformException(ConfigType valueType, ConfigType targetType)
    {
    	this.message = String.format( "The type of config value (%s) does not match the target type (%s) specified by user.", valueType, targetType);
    }
	
}
