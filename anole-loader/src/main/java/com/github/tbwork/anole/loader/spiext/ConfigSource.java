package com.github.tbwork.anole.loader.spiext;

/**
 *  Custom source to further look up configurations.
 */
public interface ConfigSource extends Sortable{


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
