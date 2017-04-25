package org.tbwork.anole.subscriber.core.impl;

import org.tbwork.anole.subscriber.core.ConfigObserver;

/**
 * Each chained configuration observer has a flag named
 * 'stopAfterProcess' which is used to tell the Anole
 * do not call next observer any more after current process
 * if you set this flag as true.
 * @author Tommy.Tang
 */
public abstract class ChainedConfigObserver implements ConfigObserver{
 
	private boolean stopAfterProcess;
	
	public ChainedConfigObserver(boolean stopAfterProcess){
		this.stopAfterProcess = stopAfterProcess;
	}
	
	public ChainedConfigObserver(){
		this.stopAfterProcess = false;
	}
	
	@Override
	public boolean stopAfterProcess(){
		return false;
	}
}
