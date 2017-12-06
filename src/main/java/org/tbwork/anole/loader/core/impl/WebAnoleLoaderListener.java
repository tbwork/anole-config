package org.tbwork.anole.loader.core.impl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.StringUtil; 

/**
 * <p> This is for web applications to load anole configurations.
 * <p> <b>Pay attention !!!</b> Dislike the spring context loader, in web 
 * mode, anole allows and only allows to load configuration files 
 * within the class path. So make sure you added your configuration
 *  files under the class path directories. Maybe you will ask why?
 * No meaningful answer, just to "make life easier".
 * <p> Usage example:
 * <p> In the <b>web.xml</b>:
 * <pre>
 *      &lt;context-param&gt;
 *    	&lt;param-name&gt;anoleConfigLocation&lt;/param-name&gt;
 *    	&lt;param-value&gt;
 *    	    	dev.anole
 *    	    	prd.anole
 *    	    	config/common.custom_suffix
 *    	&lt;/param-value&gt;
 *      &lt;/context-param&gt; 
 *      ...
 *      &lt;listener&gt;
 *            &lt;listener-class&gt;org.tbwork.anole.loader.core.impl.WebAnoleLoaderListener&lt;/listener-class&gt;
 *      &lt;/listener&gt; 
 * </pre> 
 * <p> <b>Tips:</b> If you use the default naming style of configuration
 * files which suffixes files with ".anole" and put them under the 
 * class-path folder directly, you do not have to set the anoleConfigLocation 
 * <b>context-param</b> in the web.xml. This is due to that anole will 
 * search and read configurations from all anole-style files under the 
 * class-path folder if you do not set the <b>context-param</b>.
 * <p> <b>Tips 2:</b> Make sure to put the Anole listener to the top of all
 * listener configurations so that the other frameworks can use the properties
 * loaded by Anole. Those frameworks can be Spring, Log4j, Log4j2, Logback, etc.
 *  @author Tommy.Tang
 */ 
public class WebAnoleLoaderListener extends AnoleClasspathLoader implements ServletContextListener{

	private AnoleLogger logger;
   
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		 String configLocationString =  sce.getServletContext().getInitParameter("anoleConfigLocation");
		 if(configLocationString==null || configLocationString.isEmpty()){
			 logger.warn("[!] There is no anole configuration file specified in this web application. Anole will load all *.anole files from the classpath directory.");
			 this.load();
		 }
		 else  
			 this.load(StringUtil.splitConfigLocations(configLocationString));  
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) { 
		logger.info("[:)] Application is shutting down..." ); 
	}
   
}
