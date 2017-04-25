package org.tbwork.anole.subscriber.core;
 
import org.tbwork.anole.common.model.ValueChangeDTO;

public interface ConfigObserver {

	public void process(ValueChangeDTO ccDto);
	
	public boolean stopAfterProcess();
	
}
