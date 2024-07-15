package com.github.tbwork.anole.test;

import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.AnoleApp;
import com.github.tbwork.anole.loader.util.S;
import org.junit.jupiter.api.extension.*;

/**
 * For JUnit5.
 */
public class AnoleExtension implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor, BeforeEachCallback, AfterEachCallback {

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


    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class<?> testClass = context.getRequiredTestClass();
        initializeAnole(testClass);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {

    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if(Anole.initialized)
            return;
        Class<?> testClass = context.getRequiredTestClass();
        initializeAnole(testClass);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }


    private void initializeAnole(Class<?> mainClass){
        if(!Anole.initialized){
            AnoleApp.start(mainClass);
        }
        if(Anole.getBoolProperty(ANOLE_SPRING_PROFILE_AUTO_KEY, true)){
            Anole.setSysProperty(SPRING_PROFILES_ACTIVE_KEY, Anole.getEnvironment());
        }
        else if(S.isNotEmpty(Anole.getProperty(SPRING_PROFILES_ACTIVE_KEY))){
            Anole.setSysProperty(SPRING_PROFILES_ACTIVE_KEY, Anole.getProperty(SPRING_PROFILES_ACTIVE_KEY));
        }
    }
}