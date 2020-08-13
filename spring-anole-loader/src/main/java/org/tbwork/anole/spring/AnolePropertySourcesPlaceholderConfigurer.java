package org.tbwork.anole.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.Properties;

/**
 * Specialization of {@link PlaceholderConfigurerSupport} that resolves ${...} placeholders
 * within bean definition property values and {@code @Value} annotations against the Anole configs.
 * @author Tommy.Tesla
 * @since 1.3.0
 */
@Component
public class AnolePropertySourcesPlaceholderConfigurer extends PlaceholderConfigurerSupport {

    /**
     * Processing occurs by replacing ${...} placeholders in bean definitions by resolving each
     * against the anole config source.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        final ConfigurablePropertyResolver resolver = new AnolePropertySourcesPropertyResolver();
        resolver.setPlaceholderPrefix(this.placeholderPrefix);
        resolver.setPlaceholderSuffix(this.placeholderSuffix);
        resolver.setValueSeparator(this.valueSeparator);

        final boolean ignoreUnresolvablePlaceholders = this.ignoreUnresolvablePlaceholders;
        final boolean trimValues = this.trimValues;
        final String nullValue = this.nullValue;

        StringValueResolver valueResolver =  new StringValueResolver(){
            @Override
            public String resolveStringValue(String strVal) {
                String resolved = ( ignoreUnresolvablePlaceholders ?
                        resolver.resolvePlaceholders(strVal) :
                        resolver.resolveRequiredPlaceholders(strVal));
                if (trimValues) {
                    resolved = resolved.trim();
                }
                return (resolved.equals(nullValue) ? null : resolved);
            }
        };
        doProcessProperties(beanFactory, valueResolver);
    }


    @Override
    @Deprecated
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) {
        throw new UnsupportedOperationException(
                "Call processProperties(ConfigurableListableBeanFactory, ConfigurablePropertyResolver) instead");
    }

}
