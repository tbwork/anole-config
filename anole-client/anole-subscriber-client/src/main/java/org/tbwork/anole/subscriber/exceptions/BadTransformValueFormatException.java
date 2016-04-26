package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.common.ConfigType;

public class BadTransformValueFormatException extends RuntimeException{

	private String message;
	public BadTransformValueFormatException(){
		this.message = "Error occurs when converting certain string value to certain type";
	}
	
	public BadTransformValueFormatException(String value, ConfigType type){
		this.message = String.format("Error occurs when converting '%s' value to %s", value, type);
	}
	
	
	
}
