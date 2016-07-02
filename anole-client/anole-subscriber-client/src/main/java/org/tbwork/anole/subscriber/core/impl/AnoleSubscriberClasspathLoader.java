package org.tbwork.anole.subscriber.core.impl;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.StringUtil;
import org.tbwork.anole.subscriber.client.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig; 

public class AnoleSubscriberClasspathLoader extends AnoleClasspathLoader{
 
	public AnoleSubscriberClasspathLoader(){
		super(SubscriberConfigManager.getInstance());
	}
}