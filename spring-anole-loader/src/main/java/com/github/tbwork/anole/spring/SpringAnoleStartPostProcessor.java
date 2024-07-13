package com.github.tbwork.anole.spring;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.ext.AnoleStartPostProcessor;

/**
 * This would be hooked right after Anole startup, used in Spring applications.
 */
@Component
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
