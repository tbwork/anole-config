package com.github.tbwork.anole.loader.exceptions;

import com.github.tbwork.anole.loader.util.OsUtil;

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
			return "Please set environment file first. Create a file as '/etc/anole/[env name].env' ";
		}
		case LINUX:{
			return "Please set environment file first. Create a file as '/etc/anole/[env name].env' ";
		}
		default: return null; 
	}
	}
}
