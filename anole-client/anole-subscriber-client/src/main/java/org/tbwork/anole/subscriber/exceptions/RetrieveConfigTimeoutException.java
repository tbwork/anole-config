package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.loader.types.ConfigType;

public class RetrieveConfigTimeoutException extends RuntimeException{
 
	public RetrieveConfigTimeoutException(){
		super("Timeout when retrieving config with the specified key");
	}
	
	public RetrieveConfigTimeoutException(String key){
		super(String.format("Timeout when retrieving config with the specified key = %s", key));
	}
	
	
	
}
