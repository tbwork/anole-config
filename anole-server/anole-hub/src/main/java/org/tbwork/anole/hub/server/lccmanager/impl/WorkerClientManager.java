package org.tbwork.anole.hub.server.lccmanager.impl;
 

import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.SubscriberClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClient;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.UnregisterRequest;
import org.tbwork.anole.hub.server.lccmanager.model.requests.params.IRegisterParameter;
import org.tbwork.anole.hub.server.lccmanager.model.requests.params.WorkerRegisterParameter;

import io.netty.channel.socket.SocketChannel;

@Service("workerClientManager")
public class WorkerClientManager extends LongConnectionClientManager {

	@Override
	protected LongConnectionClient createClient(int token, RegisterRequest registerRequest) {
		WorkerRegisterParameter wrp = (WorkerRegisterParameter)registerRequest.getRegisterParameter();  
		return new WorkerClient(token, registerRequest.getSocketChannel(),wrp.getIdentity());
	}

	@Override
	protected boolean validate(SocketChannel socketChannel, IRegisterParameter registerParameter) {
		WorkerRegisterParameter wrp = (WorkerRegisterParameter) registerParameter;
		String ip = socketChannel.remoteAddress().getAddress().getHostAddress();
		if(validateWorker(ip, wrp.getIdentity()))
			return true;
		return false;
	} 
	 
	
	@Override
	public void unregisterClient(UnregisterRequest request) { 
		WorkerClient lcc =  (WorkerClient) lcMap.get(request.getClientId());
		if(lcc != null){ 
			super.unregisterClient(request);
			String identity = lcc.getIdentity();
			String ip = lcc.getSocketChannel().remoteAddress().getAddress().getHostAddress();
			//delete from anole_hub where Type = 2  and address = #ip and identity = #identity;
		} 
	}
	
	private boolean validateWorker(String ip, String identity){
		//UPDATE anole_hub set Status = 1 where Address = #ip and identity = #identity and Status = 0
		return true;
	}
	
	
}
