package com.github.tbwork.anole.spring.hotmod;

import com.github.tbwork.anole.loader.spiext.AnoleUpdatePostProcessor;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.spring.hotmod.manager.BeanAutowiredValuePointManager;
import com.github.tbwork.anole.spring.hotmod.manager.impl.AnoleSpringBeanAutowiredValuePointManager;

public class RefreshSpringValuesPostProcessor implements AnoleUpdatePostProcessor {

    private final static AnoleLogger logger = new AnoleLogger(RefreshSpringValuesPostProcessor.class);

    private BeanAutowiredValuePointManager beanAutowiredValuePointManager = AnoleSpringBeanAutowiredValuePointManager.instance;

    @Override
    public boolean process(String key, String oldValue, String newValue) {
        try{
            beanAutowiredValuePointManager.updateValue(key, newValue);
            return true;
        }
        catch (Exception e){
            logger.error("Fail to refresh values for bean fields, details: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
