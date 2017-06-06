package org.tbwork.anole.subscriber.core.impl;
 
import org.tbwork.anole.loader.core.impl.AnoleClasspathLoader; 

public class AnoleSubscriberClasspathLoader extends AnoleClasspathLoader{
 
	public AnoleSubscriberClasspathLoader(){
		super(SubscriberConfigManager.getInstance());
	}
}
