package org.tbwork.anole.loader.core.resource;

import org.tbwork.anole.loader.core.model.ConfigFileResource;

/**
 * Load candidate files as configuration resources.
 */
public interface ResourceLoader {


    /**
     * Load candidate files with specified file paths as configuration resources.
     * @param configurationFilePaths  the specified file paths.
     * @return
     */
    ConfigFileResource [] load(String ... configurationFilePaths);

}
