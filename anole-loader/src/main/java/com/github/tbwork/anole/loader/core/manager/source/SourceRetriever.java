package com.github.tbwork.anole.loader.core.manager.source;

/**
 * A SourceRetriever is a specified retriever that could lookup
 * and retrieve config values from a specified source.
 */
public interface SourceRetriever {


    /**
     * Get the retriever's name
     * @return
     */
    String getName();


    /**
     * Retrieve the value of specified target key.
     * @param key the target key
     * @return the value of the target key.
     */
    String retrieve(String key);


}
