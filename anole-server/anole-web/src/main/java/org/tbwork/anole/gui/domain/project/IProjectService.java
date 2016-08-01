package org.tbwork.anole.gui.domain.project;

import java.util.List;

import org.tbwork.anole.gui.domain.model.Project;
import org.tbwork.anole.gui.domain.model.demand.CreateProjectDemand;
import org.tbwork.anole.gui.domain.model.demand.InitializeEnvsDemand;
import org.tbwork.anole.gui.domain.model.result.CreateProjectResult;
import org.tbwork.anole.gui.domain.model.result.InitializeEnvsResult;

public interface IProjectService {
 
	public CreateProjectResult createProject(CreateProjectDemand createProjectDemand);
	
	public List<Project> getProjects();
	
	public List<String> getEnvs();
	
	public List<String> getProductLines();
	
	public InitializeEnvsResult initializeEnvs(InitializeEnvsDemand envsDemand); 
	
	/**
	 * Whether the env is initialized.
	 */
	public boolean isInitialized();
	
}
