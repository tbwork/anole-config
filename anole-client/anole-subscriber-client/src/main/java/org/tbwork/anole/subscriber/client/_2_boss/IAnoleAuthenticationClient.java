package org.tbwork.anole.subscriber.client._2_boss;

import org.tbwork.anole.common.message.c_2_s.C2SMessage;

import io.netty.channel.ChannelFutureListener;

public interface IAnoleAuthenticationClient {

	/**
	 * Connect to the server.
	 */
	public void authenticate(); 
	
	/**
	 * Send a message to the server.
	 */
	public void sendMessage(C2SMessage msg);
	
	/**
	 * Send message to the server and notify specified listeners after sending.
	 */
	public void sendMessageWithListeners(C2SMessage msg, ChannelFutureListener ... listeners);
	 
	/**
	 * Save authentication information.
	 */
	public void saveToken(int clientId, int token);
}
