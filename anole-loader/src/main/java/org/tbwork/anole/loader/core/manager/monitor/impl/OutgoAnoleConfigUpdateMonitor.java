package org.tbwork.anole.loader.core.manager.monitor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.core.manager.monitor.AnoleConfigMonitor;
import org.tbwork.anole.loader.core.manager.monitor.processor.AnoleOutgoConfigUpdatePostProcessor;
import org.tbwork.anole.loader.core.model.UpdateEvent;
import org.tbwork.anole.loader.util.ProjectUtil;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
/**
 *  Monitor the changes proposed by Anole itself.
 */
public class OutgoAnoleConfigUpdateMonitor implements AnoleConfigMonitor {

    private static final Logger logger = LoggerFactory.getLogger(OutgoAnoleConfigUpdateMonitor.class);

    private Set<AnoleOutgoConfigUpdatePostProcessor> outgoUpdateProcessors = new TreeSet<AnoleOutgoConfigUpdatePostProcessor>(Comparator.comparing(o->o.getClass().getSimpleName()));

    public OutgoAnoleConfigUpdateMonitor(){
        // load post after processors via SPI.
        for (final ClassLoader classLoader : ProjectUtil.getClassLoaders()) {
            try {
                for (final AnoleOutgoConfigUpdatePostProcessor processor : ServiceLoader.load(AnoleOutgoConfigUpdatePostProcessor.class, classLoader)) {
                    outgoUpdateProcessors.add(processor);
                }
            } catch (final Throwable ex) {
                logger.error("There is something wrong occurred in loading custom post processors of config change. Details: {}", ex.getMessage());
            }
        }
    }

    @Override
    public void onEvent(UpdateEvent event, long sequence, boolean endOfBatch) throws Exception {

        outgoUpdateProcessors.forEach(processor ->{
                try {
                    processor.process(event.getKey(), event.getNewValue(), event.getCreateTime());
                }
                catch (Throwable throwable){
                    logger.error("[IMPORTANT!!!] Error occurs when {} is processing outgo update event, details: {}", processor.getClass().getSimpleName(), throwable.getMessage());
                }
           }
        );

    }


}
