package org.tbwork.anole.loader.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;  
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Anole Config Annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AnoleConfigLocation {

	/**
	 * Anole always regards customized locations as the relative paths under the classpath.
	 * You can specify locations with asterisk symbol, e.g., "dev-*.properties" will match
	 * "dev-1.properties", "dev-2.properties", "dev-hello.properties", etc.
	 * @return locations
	 */
	public String locations() default "";

	/**
	 * This means including mode will be used. In this mode, only class-paths containing
	 * the specified pattern directories can be scanned.<br/>
	 * If {@link #excludeClassPathDirectoryPattern()} is also specified, “includePathPattern” will
	 * take the priority. <br/>
	 * Generally, the more specific the pattern is, the more effective the procedure of
	 * scanning is.
	 * <br/>
	 * E.g., "soa-*" will match "/opt/user/app/soa-service/", and
	 * "/opt/user/app/xxxx-service/" will be ignored.
	 * @return pattern strings
	 */
	public String includeClassPathDirectoryPattern() default "";

	/**
	 * Opposite to the {@link #includeClassPathDirectoryPattern()}, which means the specified directory
	 * patterns would be the blacklist, according to which, those class-paths matching the patterns
	 * would be ignored. <br/>
	 * Note that if {@link #includeClassPathDirectoryPattern()} is also specified, “includePathPattern” will
	 *  take the priority. <br/>
	 *
	 * @return
	 */
	public String excludeClassPathDirectoryPattern() default "";

}
