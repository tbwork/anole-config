package com.github.tbwork.anole.loader.util.json;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Custom exclusive strategies.
 */
public class CustomExclusionStrategies  implements ExclusionStrategy {


    public CustomExclusionStrategies() {
    }


    public boolean shouldSkipField(FieldAttributes f) {

        // shield serial version uid.
        return f.getAnnotation(Ignore.class) != null || "serialVersionUID".equals(f.getName());

    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
