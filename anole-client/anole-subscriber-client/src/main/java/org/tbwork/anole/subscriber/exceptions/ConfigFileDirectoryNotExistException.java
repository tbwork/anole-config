package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.loader.types.ConfigType;
 
public class ConfigFileDirectoryNotExistException extends RuntimeException {
  
	public ConfigFileDirectoryNotExistException()
    {
    	super("Could not find the configuration file path.");
    }
	
	public ConfigFileDirectoryNotExistException(String filePath)
    {
		super(String.format("Could not find the configuration file path: %s", filePath));
    }
	 
}
