package org.tbwork.anole.spring.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.tbwork.anole.spring.AnolePropertySourcesPlaceholderConfigurer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({AnolePropertySourcesPlaceholderConfigurer.class})
public @interface EnableSpringAnole {
}
