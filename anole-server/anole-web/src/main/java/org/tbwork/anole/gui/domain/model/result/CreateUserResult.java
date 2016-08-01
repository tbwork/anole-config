package org.tbwork.anole.gui.domain.model.result;

import lombok.Data;

@Data
public class CreateUserResult { 
	private boolean success;
	private String errorMessage;  
}
