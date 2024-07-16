package com.github.tbwork.anole.loader.spiext;

import com.github.tbwork.anole.loader.AnoleApp;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.ProjectUtil;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

public class SpiExtensionManager {

    private static final AnoleLogger logger = new AnoleLogger(SpiExtensionManager.class);

    public static final Set<AnoleStartPostProcessor> anoleStartPostProcessors = new TreeSet<>(
            Comparator.comparing(Sortable::getOrder));
    public static final Set<AnoleUpdatePostProcessor> anoleUpdatePostProcessors = new TreeSet<>(
            Comparator.comparing(Sortable::getOrder));
    public static final Set<ConfigSource> configSources = new TreeSet<>(
            Comparator.comparing(Sortable::getOrder));


    public static void loadExtensionsFromSpi(){

        loadSpiInstancesByClazz(ConfigSource.class, configSources);
        loadSpiInstancesByClazz(AnoleStartPostProcessor.class, anoleStartPostProcessors);
        loadSpiInstancesByClazz(AnoleUpdatePostProcessor.class, anoleUpdatePostProcessors);

    }


    private static <T extends Sortable> void loadSpiInstancesByClazz(Class<T> clazz, Set<T> candidates){
        for (final ClassLoader classLoader : ProjectUtil.getClassLoaders()) {
            try {
                for (final T provider : ServiceLoader.load(clazz, classLoader)) {
                    candidates.add(provider);
                }
            } catch (final Throwable ex) {
                logger.warn("There is something wrong occurred in spi services lookup step. Details: {}", ex.getMessage());
            }
        }
    }

}
