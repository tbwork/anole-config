package org.tbwork.anole.hub.services.impl;

import org.springframework.stereotype.Service;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.hub.services.IUserService;


@Service("userService")
public class UserService implements IUserService{

	@Override
	public boolean verify(String username, String password, ClientType clientType) {
		// TODO Auto-generated method stub
		return false;
	}

}
