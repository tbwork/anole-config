package org.tbwork.anole.loader.core.register.converter.impl;

import org.tbwork.anole.loader.core.register.converter.IConverter;

import java.math.BigDecimal;

public class DigitalConverter implements IConverter<BigDecimal> {

    @Override
    public BigDecimal convert(String value) {
        Boolean strResult = value.matches("-?[0-9]+.?[0-9]*");
        if(strResult == true) {
            return new BigDecimal(value);
        } else {
            return null;
        }
    }
}
