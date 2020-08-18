package org.tbwork.anole.spring.hotmod.reflection.manager.impl;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.spring.hotmod.reflection.RefreshablePoint;
import org.tbwork.anole.spring.hotmod.reflection.manager.BeanAutowiredValuePointManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implement of FieldManager used to manager all refreshable field in the current application.
 * @author tommy.tesla
 * @since 1.3.0
 */
public class AnoleSpringBeanAutowiredValuePointManager implements BeanAutowiredValuePointManager {

    private Map<BeanFactory /*the key is the bean factory*/,
                Map<String /*key is the anole config key*/ , RefreshablePoint /*value is the relevant field*/>>
            beanFactoryMap = new ConcurrentHashMap<BeanFactory, Map<String, RefreshablePoint>>();


    private ConfigManager configManager = AnoleConfigManager.getInstance();

    public static final BeanAutowiredValuePointManager instance = new AnoleSpringBeanAutowiredValuePointManager();


    private AnoleSpringBeanAutowiredValuePointManager(){}


    @Override
    public void register(BeanFactory beanFactory, Field field, Object instance, String beanName, Scope scope) {

        RefreshablePoint refreshablePoint = new RefreshablePoint(field, instance, beanName, scope);
        doRegister(beanFactory, field.getAnnotation(Value.class), refreshablePoint);

    }

    @Override
    public void register(BeanFactory beanFactory, Method method, Object instance, String beanName, Scope scope) {

        RefreshablePoint refreshablePoint = new RefreshablePoint(method, instance, beanName, scope);
        doRegister(beanFactory, method.getAnnotation(Value.class), refreshablePoint);

    }

    @Override
    public void updateValue(String anoleKey, String newValue) {

        for(Map.Entry<BeanFactory, Map<String, RefreshablePoint>> entry : beanFactoryMap.entrySet()){

            BeanFactory beanFactory = entry.getKey();
            Map<String, RefreshablePoint> keyToFieldMap = entry.getValue();
            RefreshablePoint affectedField = keyToFieldMap.get(anoleKey);
            if(affectedField == null){
                continue;
            }

            Object newValueWithType = null;

            if(beanFactory instanceof ConfigurableBeanFactory){
                ConfigurableBeanFactory configurableBeanFactory =  (ConfigurableBeanFactory) beanFactory ;
                BeanExpressionContext beanExpressionContext = new BeanExpressionContext(configurableBeanFactory, affectedField.getScope());
                newValueWithType =  configurableBeanFactory.getBeanExpressionResolver().evaluate(newValue, beanExpressionContext);
            }


            try {
                affectedField.setValue(newValueWithType);
            } catch (IllegalAccessException e ) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e){
                throw new IllegalStateException(e);
            }

        }


    }


    /**
     * @param scopeBeanFactory the bean factory
     * @param valueAnnotation the @Value annotation
     * @param refreshablePoint the refreshable point
     */
    private void doRegister(BeanFactory scopeBeanFactory, Value valueAnnotation, RefreshablePoint refreshablePoint){
        if(!beanFactoryMap.containsKey(scopeBeanFactory)){
            synchronized (beanFactoryMap){
                if(!beanFactoryMap.containsKey(scopeBeanFactory)){
                    beanFactoryMap.put(scopeBeanFactory, new ConcurrentHashMap<String, RefreshablePoint>());
                }
            }
        }

        String definition = null;
        if( valueAnnotation != null){
            definition = valueAnnotation.value();
        }
        if(definition == null){
            throw new IllegalStateException("The candidate field must annotated by @Value");
        }

        String anoleKey  = refreshablePoint.getAnoleKey();

        // register to the Anole's config manager.
        configManager.registerAndSetValue(anoleKey, definition);

        beanFactoryMap.get(scopeBeanFactory).put(anoleKey, refreshablePoint);
    }

}
