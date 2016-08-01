package org.tbwork.anole.gui.presentation.model;

import java.util.List;

import org.tbwork.anole.gui.domain.model.Project;

import lombok.Data;

@Data
public class MutliResult {

	private List<Project> projects; 
	
	private List<String> prdLines;
	
	private List<String> users;
	
	private boolean loginStatus;
	
	
}
