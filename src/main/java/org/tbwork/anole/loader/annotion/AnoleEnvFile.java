package org.tbwork.anole.loader.annotion;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AnoleEnvFile {

	/**
	 * Specify a anole environment file whose content is like:
	 * environment=dev
	 */
	public String location() default "";

}
