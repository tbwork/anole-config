package org.tbwork.anole.gui.domain.model.result;

import lombok.Data;

@Data
public class AuthenUserResult {

	private boolean pass;
	private String errorMessage;
	private int permission;

}
