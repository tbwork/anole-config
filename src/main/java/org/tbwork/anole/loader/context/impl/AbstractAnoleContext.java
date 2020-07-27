package org.tbwork.anole.loader.context.impl;

import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.ConfigFileDirectoryNotExistException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.PathUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnoleContext {


    private Map<String, Boolean> alreadyFoundOrMatchedMap;

    public AbstractAnoleContext( String [] configLocations, String [] jarPatterns) {
        AnoleLoader anoleLoader = getAnoleLoader(jarPatterns);
        String [] slashProcessedPathes = PathUtil.format2SlashPathes(configLocations);
        initializeAlreadyFoundMap(slashProcessedPathes);
        anoleLoader.load(slashProcessedPathes);
    }

    protected abstract AnoleLoader getAnoleLoader(String [] jarPatterns);

    private void initializeAlreadyFoundMap(String ... configLocations) {
        if(alreadyFoundOrMatchedMap == null)
            alreadyFoundOrMatchedMap = new HashMap<String,Boolean>();
        for(String configLocation : configLocations) {
            configLocation = PathUtil.format2Slash(configLocation);
            alreadyFoundOrMatchedMap.put(configLocation, false);
        }
    }


}
