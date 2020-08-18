package org.tbwork.anole.spring;

public interface AnoleSpringConfigBuiltInKeys {

    /**
     * <p>
     *     <b>Type</b>: Boolean.
     * </p>
     * <p>
     *     <b>Note</b>: 'true' means Spring's active profile (specifically refers to 'spring.profiles.active') would be set as same as Anole's environment, and 'false' means the active profiles would be set manually. Default value is 'true'.
     * </p>
     */
    String ANOLE_SPRING_PROFILE_AUTO_KEY = "anole.spring.profile.auto";

}
