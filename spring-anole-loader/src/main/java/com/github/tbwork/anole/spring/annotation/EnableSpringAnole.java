package com.github.tbwork.anole.spring.annotation;

import com.github.tbwork.anole.spring.AnolePropertySourcesPlaceholderConfigurer;
import org.springframework.context.annotation.Import;
import com.github.tbwork.anole.spring.hotmod.reflection.AnoleSpringBeanPostProcessor;
import com.github.tbwork.anole.spring.hotmod.reflection.manager.impl.AnoleSpringBeanAutowiredValuePointManager;

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
