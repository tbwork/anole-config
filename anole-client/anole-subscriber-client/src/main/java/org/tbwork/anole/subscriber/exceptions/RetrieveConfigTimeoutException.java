package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.common.ConfigType;

public class RetrieveConfigTimeoutException extends RuntimeException{

	private String message;
	public RetrieveConfigTimeoutException(){
		this.message = "Timeout when retrieving config with the specified key";
	}
	
	public RetrieveConfigTimeoutException(String key){
		this.message = String.format("Timeout when retrieving config with the specified key = %s", key);
	}
	
	
	
}
