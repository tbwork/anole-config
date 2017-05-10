package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.loader.types.ConfigType;

public class BadTransformValueFormatException extends RuntimeException{
 
	public BadTransformValueFormatException(){
		super("Error occurs when converting certain string value to certain type");
	}
	
	public BadTransformValueFormatException(String value, ConfigType type){
		super(String.format("Error occurs when converting '%s' value to %s", value, type));
	}
	
	
	
}
