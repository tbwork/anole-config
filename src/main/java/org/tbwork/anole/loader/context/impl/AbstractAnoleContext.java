package org.tbwork.anole.loader.context.impl;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.PathUtil;
import org.tbwork.anole.loader.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnoleContext {

    private String [] configLocations;

    public AbstractAnoleContext(String [] configLocations) {
        this.configLocations = configLocations;
    }


    protected String [] getConfigLocations() {
        return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
    }

    protected abstract String [] getDefaultConfigLocations();

    /**
     * Create the anole context, this work is defined by sub-classes.
     */
    protected abstract void create();


}
