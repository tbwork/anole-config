package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.common.ConfigValueType;

public class BadTransformValueFormatException extends RuntimeException{

	private String message;
	public BadTransformValueFormatException(){
		this.message = "Error occurs when converting certain string value to certain type";
	}
	
	public BadTransformValueFormatException(String value, ConfigValueType type){
		this.message = String.format("Error occurs when converting '%s' value to %s", value, type);
	}
	
	
	
}
