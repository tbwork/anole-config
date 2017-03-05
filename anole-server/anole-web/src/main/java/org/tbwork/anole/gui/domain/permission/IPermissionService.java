package org.tbwork.anole.gui.domain.permission;

import org.tbwork.anole.gui.domain.model.demand.AssignPermissionDemand;
import org.tbwork.anole.gui.domain.model.result.AssignPermissionResult;

public interface IPermissionService {

	
	public AssignPermissionResult assignPermission(AssignPermissionDemand permissionDemand);
	
	
	/**
	 * @param project project's name
	 * @param user user's name
	 * @param env environment
	 * @return role: 0-stranger, 1-visitor, 2-manager, 3-owner
	 */
	public int getUserRole(String project, String user, String env);
	
	public boolean isOwner(String project, String user);

}
