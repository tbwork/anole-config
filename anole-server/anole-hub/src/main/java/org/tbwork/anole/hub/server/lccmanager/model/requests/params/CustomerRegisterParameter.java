package org.tbwork.anole.hub.server.lccmanager.model.requests.params;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerRegisterParameter implements IRegisterParameter{ 
	private String username;
	private String password;
}
