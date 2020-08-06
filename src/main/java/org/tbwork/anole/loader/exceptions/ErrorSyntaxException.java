package org.tbwork.anole.loader.exceptions;
 
 
public class ErrorSyntaxException extends RuntimeException {
 
	
	public ErrorSyntaxException()
    {
    	super("There is a syntax error in the file.");
    }
	
	public ErrorSyntaxException(int row, String filename, String message){
		super(String.format("There is a syntax error in file (%s) at the row = %s. Details: %s", filename, row, message));
	}
	
	public ErrorSyntaxException(int row, int col, String filename, String message){
		super(String.format("There is a syntax error at row = %s, col = %s. Details:%s ", row, col, message));
	}
	
	public ErrorSyntaxException(String key, String message){
		super(String.format("There is a syntax error while setting value for key (%s), details: %s", key, message));
	}
	 
}
