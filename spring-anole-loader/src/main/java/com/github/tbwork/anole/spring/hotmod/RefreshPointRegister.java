package com.github.tbwork.anole.spring.hotmod;

import com.github.tbwork.anole.spring.annotation.Final;
import com.github.tbwork.anole.spring.hotmod.manager.BeanAutowiredValuePointManager;
import com.github.tbwork.anole.spring.hotmod.manager.impl.AnoleSpringBeanAutowiredValuePointManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RefreshPointRegister implements BeanPostProcessor, PriorityOrdered, BeanFactoryAware {

    private BeanFactory beanFactory;

    private BeanAutowiredValuePointManager beanAutowiredValuePointManager = AnoleSpringBeanAutowiredValuePointManager.instance;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Class clazz = bean.getClass();
        if( !(beanFactory instanceof ConfigurableListableBeanFactory) ){
            return bean;
        }

        for (Field field : getFields(clazz)) {
            beanAutowiredValuePointManager.register(beanFactory, field, bean, beanName);
        }
        for (Method method : getMethods(clazz)) {
            beanAutowiredValuePointManager.register(beanFactory, method, bean, beanName);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return -50;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }


    private List<Field> getFields(Class clazz) {
        final List<Field> candidates = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, field -> {
            if(field.isAnnotationPresent(Value.class) && !field.isAnnotationPresent(Final.class) ){
                candidates.add(field);
            }
        });
        return candidates;
    }

    private List<Method> getMethods(Class clazz) {
        final List<Method> candidates = new LinkedList<>();
        ReflectionUtils.doWithMethods(clazz, method -> {
            if(method.isAnnotationPresent(Value.class) && !method.isAnnotationPresent(Final.class)){
                candidates.add(method);
            }
        });
        return candidates;
    }

}
