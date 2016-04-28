package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.common.ConfigType;
 
public class ConfigFileNotExistException extends RuntimeException {
 
	public ConfigFileNotExistException()
    {
		super("Could not find the config file.");
    }
	 
	public ConfigFileNotExistException(String filepath)
    {
		super(String.format("Could not find the config file: %s", filepath));
    }
}
