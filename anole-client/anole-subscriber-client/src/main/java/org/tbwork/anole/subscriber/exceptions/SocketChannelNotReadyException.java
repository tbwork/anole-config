package org.tbwork.anole.subscriber.exceptions;

import org.tbwork.anole.loader.types.ConfigType;
 
public class SocketChannelNotReadyException extends RuntimeException {
 
	public SocketChannelNotReadyException()
    {
		super("The anole client has not connected to the remote server yet, please connect first.");
    }
	 
}
