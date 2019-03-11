package org.tbwork.anole.loader.annotion;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AnoleClassPathFilter {

	/**
	 * Each string refers to a directory name. E.g,
	 * "abc/bcd" means the classpath must contain a
	 * sub-directory named "/abc/bcd/".
	 */
	public String [] contains() default {};

	/**
	 * Each string refers to a directory name. E.g,
	 * "forbidden" means the classpath must not contain
	 * a sub-directory named "forbidden".
	 */
	public String [] without() default {};
}
