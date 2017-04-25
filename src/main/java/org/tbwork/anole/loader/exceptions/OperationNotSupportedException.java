package org.tbwork.anole.loader.exceptions;

import org.tbwork.anole.common.ConfigType;
 
public class OperationNotSupportedException extends RuntimeException {
 
	public OperationNotSupportedException()
    {
    	super("This method is not supported in current class or object.");
    }
	 
}
