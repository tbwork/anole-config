package org.tbwork.anole.subscriber.core;

import org.tbwork.anole.common.model.ConfigModifyDTO;

public interface ConfigObserver {

	public void process(ConfigModifyDTO ccDto);
	
	public boolean stopAfterProcess();
	
}
