package org.tbwork.anole.spring;

import org.springframework.core.env.AbstractPropertyResolver;
import org.springframework.stereotype.Component;
import org.tbwork.anole.loader.Anole;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.manager.impl.AnoleConfigManager;
import org.tbwork.anole.loader.core.manager.impl.AnoleValueManager;
import org.tbwork.anole.loader.util.S;

/**
 * Used for Spring to load properties from Anole-config source.
 */
@Component
public class AnolePropertySourcesPropertyResolver extends AbstractPropertyResolver {


    private ConfigManager configManager = AnoleConfigManager.getInstance();

    @Override
    protected String getPropertyAsRawString(String key) {

        key = String.format("${%s}", key);
        AnoleValueManager.ValueDefinition valueDefinition = AnoleValueManager.compile(key, null);

        return valueDefinition.toString();

    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return convertValueIfNecessary(getPropertyAsRawString(key), targetType);
    }


}
