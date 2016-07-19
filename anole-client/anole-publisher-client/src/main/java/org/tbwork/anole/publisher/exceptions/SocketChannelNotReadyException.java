package org.tbwork.anole.publisher.exceptions;

import org.tbwork.anole.common.ConfigType;
 
public class SocketChannelNotReadyException extends RuntimeException {
 
	public SocketChannelNotReadyException()
    {
		super("The anole client has not connected to the remote server yet, please connect first.");
    }
	 
}
