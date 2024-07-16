package com.github.tbwork.anole.spring.hotmod;

import com.github.tbwork.anole.spring.annotation.Final;
import lombok.Data;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * That point could be a field or a method, which means its value is refreshable
 * according to the variable referenced by its @Value annotation's value.
 */
@Data
public class RefreshablePoint {



    /**
     * Prefix used to decorate auto created field configs' key.
     */
    String FILED_ANOLE_KEY_PREFIX = "__field__";

    /**
     * The refreshable field. Specifically refers to those fields annotated by
     * {@link org.springframework.beans.factory.annotation.Value} and not annotated
     *  by {@link Final}.
     */
    private Field field;


    /**
     * The refreshable method. Specifically refers to those methods annotated by
     * {@link org.springframework.beans.factory.annotation.Value} and not annotated
     *  by {@link Final}.
     */
    private MethodParameter methodParameter;

    /**
     * The unique key managed in Anole config.
     */
    private String anoleKey;

    /**
     * Field's owner object.
     */
    private Object ownerInstance;

    /**
     * The owner bean's name.
     */
    private String beanName;

    /**
     * Field's type.
     */
    private Class valueType;


    public RefreshablePoint(Method method, Object ownerInstance, String beanName){
        this.methodParameter = new MethodParameter(method, 0);
        this.valueType = this.methodParameter.getParameterType();
        this.ownerInstance = ownerInstance;
        this.beanName = beanName;
        this.anoleKey = String.format("%s.%s.%s", FILED_ANOLE_KEY_PREFIX, beanName, field.getName());
    }

    public RefreshablePoint(Field field, Object ownerInstance, String beanName){
        this.field = field;
        this.valueType = field.getType();
        this.ownerInstance = ownerInstance;
        this.beanName = beanName;
        this.anoleKey = String.format("%s.%s.%s", FILED_ANOLE_KEY_PREFIX, beanName, field.getName());
    }

    public void setValue(Object value) throws IllegalAccessException, InvocationTargetException {

        if(field == null){
            injectByMethod(value);
        }
        else{
            injectField(value);
        }

    }

    private void injectField(Object value) throws IllegalAccessException{
        boolean accessible = field.isAccessible();

        if(!accessible){
            field.setAccessible(true);
        }

        field.set(ownerInstance, value);

        if(!accessible){
            field.setAccessible(false);
        }
    }

    private void injectByMethod(Object newVal) throws InvocationTargetException, IllegalAccessException {
        methodParameter.getMethod().invoke(ownerInstance, newVal);
    }

}
