package org.tbwork.anole.loader.exceptions;
 
public class OperationNotSupportedException extends RuntimeException {
 
	public OperationNotSupportedException()
    {
    	super("This method is not supported in current class or object.");
    }

    public OperationNotSupportedException(String message)
    {
        super(message);
    }
	 
}
