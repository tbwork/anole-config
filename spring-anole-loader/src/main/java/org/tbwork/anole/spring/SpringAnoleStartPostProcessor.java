package org.tbwork.anole.spring;

import org.springframework.util.StringUtils;
import org.tbwork.anole.loader.Anole;
import org.tbwork.anole.loader.ext.AnoleStartPostProcessor;

/**
 * This would be hooked right after Anole startup, used in Spring applications.
 */
public class SpringAnoleStartPostProcessor implements AnoleStartPostProcessor {

    private static final String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";

    @Override
    public void execute() {
        if(Anole.getBoolProperty(AnoleSpringConfigBuiltInKeys.ANOLE_SPRING_PROFILE_AUTO_KEY, true)){
            Anole.setSysProperty(SPRING_PROFILES_ACTIVE_KEY, Anole.getEnvironment());
        }
        else if(StringUtils.hasText(Anole.getProperty(SPRING_PROFILES_ACTIVE_KEY))){
            Anole.setSysProperty(SPRING_PROFILES_ACTIVE_KEY, Anole.getProperty(SPRING_PROFILES_ACTIVE_KEY));
        }
    }

}
