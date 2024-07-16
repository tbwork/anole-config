package com.github.tbwork.anole.spring.annotation;

import com.github.tbwork.anole.spring.SpringProfileAnoleActivator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import( {
        SpringProfileAnoleActivator.class
})
public @interface UsingAnoleEnvAsProfile {
}
