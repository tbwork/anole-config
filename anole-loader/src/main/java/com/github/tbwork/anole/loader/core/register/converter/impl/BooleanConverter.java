package com.github.tbwork.anole.loader.core.register.converter.impl;

import com.github.tbwork.anole.loader.core.register.converter.IConverter;

public class BooleanConverter implements IConverter<Boolean> {

    @Override
    public Boolean convert(String value) {
        value = value.trim().toLowerCase();
        if(!"true".equals(value) && !"false".equals(value) && !"0".equals(value) && !"1".equals(value))
            return null;
        return "true".equals(value) || "1".equals(value);
    }


}
