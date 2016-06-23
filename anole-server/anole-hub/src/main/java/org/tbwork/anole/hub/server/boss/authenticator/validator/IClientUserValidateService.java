package org.tbwork.anole.hub.server.boss.authenticator.validator;

public interface IClientUserValidateService {
 
	public boolean validateUser(String username, String password);
	
}
