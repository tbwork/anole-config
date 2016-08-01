package org.tbwork.anole.gui.domain.user;

import java.util.List;

import org.tbwork.anole.gui.domain.model.demand.AuthenUserDemand;
import org.tbwork.anole.gui.domain.model.demand.CreateUserDemand;
import org.tbwork.anole.gui.domain.model.demand.ModifyPasswordDemand;
import org.tbwork.anole.gui.domain.model.result.AuthenUserResult;
import org.tbwork.anole.gui.domain.model.result.CreateUserResult;
import org.tbwork.anole.gui.domain.model.result.ModifyPasswordResult;

public interface IUserService {

	/**
	 * Add a new user.
	 */
	public CreateUserResult createUser(CreateUserDemand createUserDemand);
	
	
	/**
	 * Modify password.
	 */
	public ModifyPasswordResult modifyPassword(ModifyPasswordDemand modifyUserDemand);
	
	/**
	 * Authenticate a user.
	 */
	public AuthenUserResult authenUser(AuthenUserDemand auth);
	
	/**
	 * Whether the Anole is initialized.
	 */
	public boolean isInitialized();
	
	/**
	 * Get users.
	 * @return
	 */
	public List<String> getUsers();
	
}
