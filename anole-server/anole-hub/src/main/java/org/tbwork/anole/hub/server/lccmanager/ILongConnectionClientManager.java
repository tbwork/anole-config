package org.tbwork.anole.hub.server.lccmanager;
 
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.ValidateRequest;
import org.tbwork.anole.hub.server.lccmanager.model.response.RegisterResult;

import io.netty.channel.socket.SocketChannel;

/**
 * The manager of clients, who offers register, unregister
 * , validate, ackPing and promisePingAndScavenge operations.
 * @see #registerClient(BaseOperationRequest)
 * @see #unregisterClient(BaseOperationRequest)
 * @see #validate(BaseOperationRequest)
 * @see #ackPing(int)
 * @see #promisePingAndScavenge()
 * @author tommy.tang
 */
public interface ILongConnectionClientManager {

	/**
	 * Validate whether a client exists and is valid.
	 * @param request the request.
	 * @return true if the client exists and is valid.
	 */
	public boolean validate(ValidateRequest request) ;

	/**
	 * Unregister a client.
	 * @param request the request.
	 */
	public void unregisterClient(UnregisterRequest request);
	
	/**
	 * Register a new client, after which the client can use
	 * token to communication with the server.
	 * @param request the request.
	 */
	public RegisterResult registerClient(RegisterRequest request); 
	
	
	/**
	 * Call this if the server actually received a ping from 
	 * the client with specified clientId.
	 * @param clientId 
	 */
	public void ackPing(int clientId);
	
	
	/**
	 * Promise that a ping from each client would be received,
	 * and then clean up all dead clients.
	 */
	public void promisePingAndScavenge(String clientName);
	
	
	/**
	 * Get the count of client.
	 */
	public int getClientCount();
}
