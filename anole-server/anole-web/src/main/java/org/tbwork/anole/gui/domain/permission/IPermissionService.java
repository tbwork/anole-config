package org.tbwork.anole.gui.domain.permission;

import org.tbwork.anole.gui.domain.model.demand.AssignPermissionDemand;
import org.tbwork.anole.gui.domain.model.result.AssignPermissionResult;

public interface IPermissionService {

	
	public AssignPermissionResult assignPermission(AssignPermissionDemand permissionDemand);
	
	
	/**
	 * @param project
	 * @param user
	 * @param env
	 * @return role: 0-stranger, 1-visitor, 2-manager, 3-admin
	 */
	public int getPermission(String project, String user, String env);
	
	public boolean isOwner(String project, String user);

}
