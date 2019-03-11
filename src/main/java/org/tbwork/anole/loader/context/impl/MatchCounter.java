package org.tbwork.anole.loader.context.impl;

import org.tbwork.anole.loader.util.AnoleLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: anole-loader
 * @description: Match counter
 * @author: tommy.tb
 * @create: 2019-03-06 09:40
 **/
public class MatchCounter {


    private static final Map<String, String> patternConfigMap = new HashMap<String, String>();

    private static final Map<String,Boolean> counterMap = new HashMap<String,Boolean>();

    public static void putConfigMap(String fullpathPattern, String userSpecifiedLocation ){
        patternConfigMap.put(fullpathPattern, userSpecifiedLocation);
    }

    public static void setFoundFlag(String fullpathPattern){
        counterMap.put(patternConfigMap.get(fullpathPattern), true);
    }

    public static void initialize(List<String> userInputConfigLocations){
        for(String location : userInputConfigLocations){
            counterMap.put(location, false);
        }
    }

    public static void checkNotExist(){
        if(AnoleLogger.isDebugEnabled()){
            for(Map.Entry<String, Boolean> item: counterMap.entrySet()){
                if(item.getValue()!=null && !item.getValue())
                    AnoleLogger.debug("There is no matched file for '{}'", item.getKey());
            }
        }
    }

}
