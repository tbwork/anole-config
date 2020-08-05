package org.tbwork.anole.loader.core.register.converter;

public interface IConverter<T> {
     
    <T> T convert(String value);

}
