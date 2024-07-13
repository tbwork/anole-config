package com.github.tbwork.anole.loader.context;

/**
 * Anole context manages an Anole instance, and this is where every thin starts.
 */
public interface AnoleContext {

    /**
     * Close the Anole context gracefully.
     * @return get the current environment.
     */
    void close();

    /**
     * Get environment.
     * @return get the current environment.
     */
    String getEnvironment();

}
