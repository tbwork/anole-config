package org.tbwork.anole.hub.services.impl;

import org.anole.infrastructure.dao.AnoleUserMapper;
import org.anole.infrastructure.model.AnoleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.hub.services.IUserService;
import org.tbwork.anole.hub.util.StringUtil;


@Service("userService")
public class UserService implements IUserService{

	@Autowired
	private AnoleUserMapper anoleUserDao;
	
	@Override
	public boolean verify(String username, String password, ClientType clientType) {
		AnoleUser anoleUser = anoleUserDao.selectByUsername(username);
		if(anoleUser != null){
			String pwdMd5 = anoleUser.getPassword();
			String inputPwdMd5 = StringUtil.md5(password); 
			return pwdMd5.equals(inputPwdMd5);
		}
		return false;
	}

}
