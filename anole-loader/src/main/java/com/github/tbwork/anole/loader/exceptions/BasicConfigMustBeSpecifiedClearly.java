package com.github.tbwork.anole.loader.exceptions;

public class BasicConfigMustBeSpecifiedClearly extends RuntimeException {

    public BasicConfigMustBeSpecifiedClearly(String key){
        super(String.format("The key named (%s) is a basic property used in the startup stage, please make sure it is" +
                        " defined directly without any other variable in it.", key));
    }

    public BasicConfigMustBeSpecifiedClearly(String key, String example)
    {
        super(String.format("The key named (%s) is a basic property used in the startup stage, please make sure it is" +
                        " defined directly without any other variable in it. For example, '(%s)=%s' is " +
                        "right; '(%s)=${variable}' is wrong.",
                key, example, key, key));
    }
}
