package org.tbwork.anole.gui.domain.authorization;

import org.tbwork.anole.gui.domain.enums.Operation;

public interface IAuthorizationService { 
	
	public void authenAdminOperation(String opeartor, Operation operation);
	
	public void authen(String operator, String project, Operation operation);
}
