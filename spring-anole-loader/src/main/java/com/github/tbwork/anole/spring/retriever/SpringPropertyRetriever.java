package com.github.tbwork.anole.spring.retriever;

import com.github.tbwork.anole.loader.AnoleApp;
import com.github.tbwork.anole.loader.core.manager.source.ConfigSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import com.github.tbwork.anole.loader.Anole;


@Component
public class SpringPropertyRetriever implements EnvironmentPostProcessor, ConfigSource, Ordered {

    // Before ConfigFileApplicationListener
    private int order = Integer.MAX_VALUE;

    private static volatile ConfigurableEnvironment environment = null;

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        SpringPropertyRetriever.environment = environment;
        if(!Anole.initialized){
            AnoleApp.start();
        }
        else{
            Anole.refreshContext(true);
        }
    }

    private String getProperty(String key){
        if(SpringPropertyRetriever.environment != null)
            return SpringPropertyRetriever.environment.getProperty(key);
        else
            return null;
    }

    @Override
    public String getName() {
        return "Spring";
    }

    @Override
    public String retrieve(String key) {
        return getProperty(key);
    }
}
