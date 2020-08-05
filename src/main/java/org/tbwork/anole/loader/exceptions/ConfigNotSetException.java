package org.tbwork.anole.loader.exceptions;

public class ConfigNotSetException extends RuntimeException {

    public ConfigNotSetException(String key)
    {
        super(String.format("Could not find the config named (%s), please set it first", key));
    }
}
