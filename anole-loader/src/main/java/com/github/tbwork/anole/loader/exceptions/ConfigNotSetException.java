package com.github.tbwork.anole.loader.exceptions;

public class ConfigNotSetException extends RuntimeException {

    public ConfigNotSetException(String key)
    {
        new ConfigNotSetException(key, "please set it first");
    }

    public ConfigNotSetException(String key, String detail)
    {
        super(String.format("Could not find the config named (%s), note: %s", key, detail));
    }
}
