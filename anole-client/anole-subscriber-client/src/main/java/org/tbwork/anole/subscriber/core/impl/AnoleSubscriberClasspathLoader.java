package org.tbwork.anole.subscriber.core.impl;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.StringUtil;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig;
import org.tbwork.anole.subscriber.core.AnoleSubscriberLoader; 

public class AnoleSubscriberClasspathLoader extends AnoleSubscriberFileSystemLoader{
 
	@Override
	public void load() {
		load("*.anole");       // load local configurations 
	} 
	
	@Override
	public void load(String... configLocations) {
		configLocations = StringUtil.prefixString(configLocations, ProjectUtil.classPath);
		super.load(configLocations); 
	} 
}
