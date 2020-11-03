package org.tbwork.anole.loader.util.json;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this to ignore certain field while serializing the object
 * to JSON string.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Ignore {
    // Field tag only annotation
}
