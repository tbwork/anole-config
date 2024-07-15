package com.github.tbwork.anole.spring.annotation;
import com.github.tbwork.anole.spring.AnoleSpringStarter;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import( {
        AnoleSpringStarter.class
})
public @interface EnableSpringAnole{
}
