package org.tbwork.anole.hub.server.boss.authenticator.validator.impl;

import org.springframework.stereotype.Service;
import org.tbwork.anole.hub.server.boss.authenticator.validator.IClientUserValidateService;  

@Service("clientUserValidateService")
public class ClientUserValidateService implements IClientUserValidateService {

	@Override
	public boolean validateUser(String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
