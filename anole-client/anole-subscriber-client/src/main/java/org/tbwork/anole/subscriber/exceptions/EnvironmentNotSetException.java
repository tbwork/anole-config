package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.subscriber.util.OsUtil;
 
public class EnvironmentNotSetException extends RuntimeException {

	private final static String errorMessage = getErrorMessage();
	
	public EnvironmentNotSetException()
    {
		super(errorMessage);    	
    }
	 
	public static String getErrorMessage(){
		switch(OsUtil.getOsCategory()){
		case WINDOWS:{
			return "Please set environment file first. Create a file as 'C://anole/[env name].env' ";
		}
		case MAC:{
			return "Please set environment file first. Create a file as 'C://anole/[env name].env' ";
		}
		case LINUX:{
			return "Please set environment file first. Create a file as '/etc/anole/[env name].env' ";
		}
		default: return null; 
	}
	}
}
