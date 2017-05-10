package org.tbwork.anole.hub.exceptions;

import org.tbwork.anole.loader.types.ConfigType;
 
public class InstanceNotMatchTheTypeException extends RuntimeException {

	private static final String errorMessage = "The instance's type (= %s) is not match with the target type (=%s).";
	
	public InstanceNotMatchTheTypeException()
    {
    	super(String.format(errorMessage,"unknown", "unknown"));
    }
	
	public InstanceNotMatchTheTypeException(String instanceType, String targetType)
    {
    	super(String.format(errorMessage, instanceType, targetType));
    }
	 
}
