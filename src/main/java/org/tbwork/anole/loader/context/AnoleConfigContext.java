package org.tbwork.anole.loader.context;

import org.tbwork.anole.loader.annotion.AnoleConfigLocation;
import org.tbwork.anole.loader.context.impl.AnoleClasspathConfigContext;
import org.tbwork.anole.loader.context.impl.AnoleFileConfigContext;

/**
 * <p>Before using Anole to manage your configuration, 
 * you should create the configuration context. It is
 * recommended to use the {@link AnoleConfigLocation} to
 * setup your application. However in some case you may 
 * need to create the configuration context directly.
 * <p>Usage example:
 *    	
 * <pre>
 *    AnoleConfigContext acc = new AnoleClasspathConfigContext(LogLevel.INFO, configFilePath);
 *    //use AnoleLocalConfig as you like.
 * </pre>
 * <p> <b>About LogLevel:</b> The anole does not use any log implement
 * in the startup stage, it only providers the standard output on the
 * console window. After startup, it would use SLF4J facade to print logs
 * after when the logging framework (e.g., log4j,log4j2,log-back) you 
 * configured will be used. 
 * @author Tommy.Tang
 * @see AnoleFileConfigContext
 * @see AnoleClasspathConfigContext 
 */
public interface AnoleConfigContext {

}
