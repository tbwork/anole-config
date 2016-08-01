package org.tbwork.anole.gui.domain.authorization.impl;

import org.anole.infrastructure.dao.AnoleProjectMapper;
import org.anole.infrastructure.model.AnoleProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tbwork.anole.gui.domain.authorization.IAuthorizationService;
import org.tbwork.anole.gui.domain.enums.Operation;
import org.tbwork.anole.gui.domain.exceptions.OperatorNoRightException;
import org.tbwork.anole.gui.domain.project.IProjectService;

@Service
public class AuthorizationService implements IAuthorizationService {
 
	@Autowired
	private IProjectService ps; 
	
	@Autowired
	private AnoleProjectMapper anoleProjectMapper;

	@Override
	public void authenAdminOperation(String operator, Operation operation) {
		if(operation == Operation.CREATE_USER || operation == Operation.MODIFY_PASSWORD ){
			if(!operator.equals("admin"))
				throw new OperatorNoRightException();
		}
	}

	@Override
	public void authen(String operator, String project, Operation operation) {
		if(operator.equals("admin"))
			return;
		if(operation == Operation.ASSIGN_PERMISSION)// project's owner can assign the permission of accessing the project
		{
			AnoleProject ap = anoleProjectMapper.selectByName(project);
			if(ap == null || !ap.getOwner().equals(operator))
				throw new OperatorNoRightException();
		} 
	}

}
