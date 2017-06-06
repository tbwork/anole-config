package org.tbwork.anole.loader.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;  
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy; 

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AnoleConfigLocation {
	
	public String locations() default "";
	
}
