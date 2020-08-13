package org.tbwork.anole.spring.hotmod.reflection.manager;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.Scope;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Field manager takes charge of all operations to fields registered into.
 */
public interface BeanFieldManager {


    /**
     * Register the bean's fields annotated by @Value to the manager.
     *
     * @param beanFactory the factory from where the bean is created
     * @param field the refreshable field
     * @param instance the bean instance
     * @param beanName the bean name
     * @param scope the bean's scope
     */
    void register(BeanFactory beanFactory, Field field, Object instance, String beanName, Scope scope);


    /**
     * Register the bean's methods annotated by @Value to the manager.
     *
     * @param beanFactory the factory from where the bean is created
     * @param method the refreshable method
     * @param instance the bean instance
     * @param beanName the bean name
     * @param scope the bean's scope
     */
    void register(BeanFactory beanFactory, Method method, Object instance, String beanName, Scope scope);

    /**
     * Update the given anoleKey's all relevant fields of beans with the given new value.
     * @param anoleKey the given anole config key
     * @param newValue the new value
     */
    void updateValue(String anoleKey, String newValue);

}
