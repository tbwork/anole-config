package org.tbwork.anole.subscriber.core.impl;

import org.tbwork.anole.loader.core.AnoleLoader;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader;
import org.tbwork.anole.loader.core.impl.AnoleFileSystemLoader;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleConfig;
import org.tbwork.anole.subscriber.core.AnoleSubscriberLoader;

public class AnoleSubscriberFileSystemLoader implements AnoleSubscriberLoader{

	private static AnoleSubscriberClient client = AnoleSubscriberClient.instance(); 
	private AnoleLoader anoleLoader = new AnoleFileSystemLoader();
	
	
	@Override
	public void load() {
		anoleLoader.load();       // load local configurations
		postLoad();
	} 
	
	@Override
	public void load(String... configLocations) {
		anoleLoader.load(configLocations);
		postLoad();
	}
	
	
	private void postLoad(){
		client.connect();   // start the anole subscriber client 
		new AnoleConfig();  // to call static blocks of AnoleConfig to set new configuration manager.
	}
}
