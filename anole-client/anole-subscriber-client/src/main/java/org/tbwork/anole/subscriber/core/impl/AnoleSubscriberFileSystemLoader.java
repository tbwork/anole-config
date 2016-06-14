package org.tbwork.anole.subscriber.core.impl;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.core.impl.AnoleFileSystemLoader;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig; 

public class AnoleSubscriberFileSystemLoader extends AnoleFileSystemLoader{

	 public AnoleSubscriberFileSystemLoader(){
		 super(SubscriberConfigManager.getInstance());
	 }
 
}
