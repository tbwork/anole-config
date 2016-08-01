package org.tbwork.anole.gui.domain.exceptions;
 
 
public class OperatorNoRightException extends RuntimeException {

	private static final String errorMessage = "Permission denied!";
	
	public OperatorNoRightException()
    {
    	super(String.format(errorMessage));
    }
	 
}
