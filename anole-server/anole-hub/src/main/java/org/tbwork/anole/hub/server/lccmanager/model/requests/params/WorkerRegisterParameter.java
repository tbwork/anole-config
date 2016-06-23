package org.tbwork.anole.hub.server.lccmanager.model.requests.params;

import lombok.Data;

@Data
public class WorkerRegisterParameter implements IRegisterParameter{ 
	private String identity;
}
