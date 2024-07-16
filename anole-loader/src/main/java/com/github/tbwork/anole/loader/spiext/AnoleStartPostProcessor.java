package com.github.tbwork.anole.loader.spiext;

/**
 *  Anole start would hook that allows for custom modification of an Anole's
 *  configs.
 *  @author tommy.tesla
 */
public interface AnoleStartPostProcessor extends Sortable{


    /**
     * Tasks need to be executed right after Anole start-up run here.
     */
    boolean process();

}
