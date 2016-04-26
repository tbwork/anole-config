package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.common.ConfigType;
 
public class NotReadyToRetrieveRemoteConfigException extends RuntimeException {

	private String message;
	
	public NotReadyToRetrieveRemoteConfigException()
    {
    	this.message = "The client is not ready to retrieve remote configuration due to that there is no ConfigItem indexed by the key locally, please make sure there is a blank ConfigItem indexed by the key in the configMap at least";
    }
	
	public NotReadyToRetrieveRemoteConfigException(String key)
    {
    	this.message = String.format("The client is not ready to retrieve remote configuration due to that there is no ConfigItem indexed by the key (%s) locally, please make sure there is a blank ConfigItem indexed by the key (%s) in the configMap at least",
    			                     key, key);
    }
	
}
