package org.tbwork.anole.loader.exceptions;
 
 
public class CircularDependencyException extends RuntimeException {
 
	public CircularDependencyException()
    {
		super("The config (key = %s) can not rely on itself directly or mediately such us: a = #{a}");
    }
	
    public CircularDependencyException(String key)
    {
    	super(String.format( "The config (key = %s) can not rely on itself directly or mediately such us: a = #{a}", key));
    }
	
}
