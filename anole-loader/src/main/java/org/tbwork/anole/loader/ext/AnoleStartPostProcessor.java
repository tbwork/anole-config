package org.tbwork.anole.loader.ext;

/**
 *  Anole start would hook that allows for custom modification of an Anole's
 *  configs.
 *  @author tommy.tesla
 */
public interface AnoleStartPostProcessor {


    /**
     * Tasks need to be executed right after Anole start-up run here.
     */
    void execute();

}
