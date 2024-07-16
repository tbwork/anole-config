package com.github.tbwork.anole.spring;

import com.github.tbwork.anole.loader.Anole;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

public class SpringProfileAnoleActivator implements ApplicationListener<ApplicationStartingEvent> {


    private static final String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";
    /**
     * <p>
     *     <b>Type</b>: Boolean.
     * </p>
     * <p>
     *     <b>Note</b>: 'true' means Spring's active profile (specifically refers to 'spring.profiles.active')
     *     would be set as same as Anole's environment, and 'false' means the active profiles would be set manually.
     *     Default value is 'true'.
     * </p>
     */
    String ANOLE_SPRING_PROFILE_AUTO_KEY = "anole.spring.profile.auto";

    public SpringProfileAnoleActivator() {

    }

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        if(!Anole.initialized){
            throw new RuntimeException("Please make sure you've already started the Anole context by AnoleApp.start() or @AnoleTest (for JUnit5 tests).");
        }

        if(Anole.getBoolProperty(ANOLE_SPRING_PROFILE_AUTO_KEY, true)){
            Anole.setSysProperty(SPRING_PROFILES_ACTIVE_KEY, Anole.getEnvironment());
        }
        else if(StringUtils.hasText(Anole.getProperty(SPRING_PROFILES_ACTIVE_KEY))){
            Anole.setSysProperty(SPRING_PROFILES_ACTIVE_KEY, Anole.getProperty(SPRING_PROFILES_ACTIVE_KEY));
        }
    }
}