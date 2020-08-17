package org.tbwork.anole.spring.hotmod.reflection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.tbwork.anole.spring.annotation.Final;
import org.tbwork.anole.spring.hotmod.reflection.manager.BeanAutowiredValuePointManager;
import org.tbwork.anole.spring.hotmod.reflection.manager.impl.AnoleSpringBeanAutowiredValuePointManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class AnoleSpringBeanPostProcessor implements BeanPostProcessor, PriorityOrdered, BeanFactoryAware {

    private BeanFactory beanFactory;

    private BeanAutowiredValuePointManager beanAutowiredValuePointManager = AnoleSpringBeanAutowiredValuePointManager.instance;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Class clazz = bean.getClass();
        if( !(beanFactory instanceof ConfigurableListableBeanFactory) ){
            return bean;
        }
        Scope scope = getScopeOfBean(beanName);
        for (Field field : getFields(clazz)) {
            beanAutowiredValuePointManager.register(beanFactory, field, bean, beanName, scope);
        }
        for (Method method : getMethods(clazz)) {
            beanAutowiredValuePointManager.register(beanFactory, method, bean, beanName, scope);
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

    private Scope getScopeOfBean(String beanName){
        if(!(beanFactory instanceof ConfigurableListableBeanFactory)){
            return null;
        }
        ConfigurableListableBeanFactory configurableListableBeanFactory = ((ConfigurableListableBeanFactory)beanFactory);
        BeanDefinition beanDefinition = configurableListableBeanFactory.getMergedBeanDefinition(beanName);
        if(beanDefinition == null){
            return null;
        }
        return configurableListableBeanFactory.getRegisteredScope(beanDefinition.getScope());
    }
}
