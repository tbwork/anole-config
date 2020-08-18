package org.tbwork.anole.spring.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.tbwork.anole.spring.AnolePropertySourcesPlaceholderConfigurer;
import org.tbwork.anole.spring.hotmod.reflection.AnoleSpringBeanPostProcessor;
import org.tbwork.anole.spring.hotmod.reflection.manager.impl.AnoleSpringBeanAutowiredValuePointManager;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import( {
        AnolePropertySourcesPlaceholderConfigurer.class,
        AnoleSpringBeanAutowiredValuePointManager.class,
        AnoleSpringBeanPostProcessor.class
})
public @interface EnableSpringAnole {
}
