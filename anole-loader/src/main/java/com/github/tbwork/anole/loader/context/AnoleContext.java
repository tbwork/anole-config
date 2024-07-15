package com.github.tbwork.anole.loader.context;

/**
 * Anole context manages an Anole instance, and this is where every thin starts.
 */
public interface AnoleContext {

    /**
     * Get environment.
     * @return get the current environment.
     */
    String getEnvironment();

}
