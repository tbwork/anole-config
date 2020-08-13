package org.tbwork.anole.spring;

import org.springframework.core.env.AbstractPropertyResolver;
import org.tbwork.anole.loader.Anole;

/**
 * Used for Spring to load properties from Anole-config source.
 */
public class AnolePropertySourcesPropertyResolver extends AbstractPropertyResolver {
    @Override
    protected String getPropertyAsRawString(String key) {
        return Anole.getProperty(key);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return convertValueIfNecessary(getPropertyAsRawString(key), targetType);
    }

}
