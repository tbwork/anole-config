package com.github.tbwork.anole.loader.exceptions;

public class ConfigTypeNotMatchedException extends RuntimeException {

    public ConfigTypeNotMatchedException(String value, String typeName)
    {
    	super(String.format( "The value (%s) can not be converted to %s type.", value, typeName));
    }
	
}
