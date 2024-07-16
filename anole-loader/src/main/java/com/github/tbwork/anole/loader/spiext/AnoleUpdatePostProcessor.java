package com.github.tbwork.anole.loader.spiext;

public interface AnoleUpdatePostProcessor extends Sortable{

    boolean process(String key, String oldValue, String newValue);

}
