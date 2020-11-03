package org.tbwork.anole.loader.core.register.converter.impl;

import org.tbwork.anole.loader.core.register.converter.IConverter;
import org.tbwork.anole.loader.util.S;

import java.math.BigDecimal;

public class DigitalConverter implements IConverter<BigDecimal> {

    @Override
    public BigDecimal convert(String value) {
        if(S.isEmpty(value)){
            return null;
        }
        Boolean strResult = value.matches("-?\\d+(\\.\\d+)?");
        if(strResult == true) {
            try{
                return new BigDecimal(value);
            }
            catch (Exception e){
                return null;
            }
        } else {
            if(value.startsWith("0x") || value.startsWith("0X")){
                return convert(value.substring(2).trim());
            }
            return null;
        }
    }

}
