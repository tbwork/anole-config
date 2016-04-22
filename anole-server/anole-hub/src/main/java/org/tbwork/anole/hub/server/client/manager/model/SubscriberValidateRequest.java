package org.tbwork.anole.hub.server.client.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriberValidateRequest implements BaseOperationRequest{ 
	private int clientId;
	private int token;  
}
