package org.tbwork.anole.subscriber.core;

import org.tbwork.anole.common.model.ConfigChangeDTO;

public interface ConfigObserver {

	public void process(ConfigChangeDTO ccDto);
	
	public boolean stopAfterProcess();
	
}
