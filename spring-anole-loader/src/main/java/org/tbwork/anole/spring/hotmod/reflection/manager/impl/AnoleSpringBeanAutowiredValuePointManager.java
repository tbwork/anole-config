package org.tbwork.anole.spring.hotmod.reflection.manager.impl;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.*;
import org.springframework.stereotype.Component;
import org.tbwork.anole.loader.Anole;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.spring.hotmod.reflection.RefreshablePoint;
import org.tbwork.anole.spring.hotmod.reflection.manager.BeanAutowiredValuePointManager;
import org.tbwork.anole.spring.util.TypeUtils;

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
@Component
public class AnoleSpringBeanAutowiredValuePointManager implements BeanAutowiredValuePointManager {

    private Map<BeanFactory /*the key is the bean factory*/,
                Map<String /*key is the anole config key*/ , RefreshablePoint /*value is the relevant field*/>>
            beanFactoryMap = new ConcurrentHashMap<BeanFactory, Map<String, RefreshablePoint>>();


    private ConfigManager configManager = AnoleConfigManager.getInstance();

    public static final BeanAutowiredValuePointManager instance = new AnoleSpringBeanAutowiredValuePointManager();


    private AnoleSpringBeanAutowiredValuePointManager(){}


    @Override
    public void register(BeanFactory beanFactory, Field field, Object instance, String beanName) {

        RefreshablePoint refreshablePoint = new RefreshablePoint(field, instance, beanName);
        doRegister(beanFactory, field.getAnnotation(Value.class), refreshablePoint);

    }

    @Override
    public void register(BeanFactory beanFactory, Method method, Object instance, String beanName) {

        RefreshablePoint refreshablePoint = new RefreshablePoint(method, instance, beanName);
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
                ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
                Scope beanScope = getScopeOfBean(configurableBeanFactory, affectedField.getBeanName());
                BeanExpressionContext beanExpressionContext = new BeanExpressionContext(configurableBeanFactory, beanScope);
                newValueWithType =  configurableBeanFactory.getBeanExpressionResolver().evaluate(newValue, beanExpressionContext);
            }

            newValueWithType = TypeUtils.convert(newValueWithType, affectedField.getValueType());

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

        // retrieve the real definition
        definition = registerDefaultValueAndGetAnoleStyleDefinition(definition);

        // register to the Anole's config manager.
        configManager.registerAndSetValue(anoleKey, definition);

        beanFactoryMap.get(scopeBeanFactory).put(anoleKey, refreshablePoint);
    }


    private String registerDefaultValueAndGetAnoleStyleDefinition(String definition){
        definition = definition.trim();
        if(! (definition.startsWith("${") && definition.endsWith("}"))){
            return definition;
        }
        int index = definition.indexOf(":");
        if( index < 0){
            return definition;
        }

        String referencingVariable = definition.substring(2, index).trim();
        String defaultValue = definition.substring(index+1, definition.length()-1).trim();

        if(defaultValue.startsWith("${")&& defaultValue.endsWith("}")){
            defaultValue = registerDefaultValueAndGetAnoleStyleDefinition(defaultValue);
        }
        // if the referenced config does not has any value, using the default.
        if(Anole.isPropertyEmptyOrNotExist(referencingVariable)){
            // register the referenced config to the Anole's config manager.
            configManager.registerAndSetValue(referencingVariable, defaultValue);
        }

        return String.format("${%s}", referencingVariable);
    }


    private Scope getScopeOfBean(ConfigurableBeanFactory beanFactory, String beanName){
        if(!(beanFactory instanceof ConfigurableListableBeanFactory)){
            return null;
        }
        BeanDefinition beanDefinition = beanFactory.getMergedBeanDefinition(beanName);
        if(beanDefinition == null){
            return null;
        }
        return beanFactory.getRegisteredScope(beanDefinition.getScope());
    }
}
