package com.github.tbwork.anole.loader.core.loader.impl;


import com.github.tbwork.anole.loader.core.register.AnoleConfigRegister;
import com.github.tbwork.anole.loader.core.resource.impl.FileResourceLoader;

public class AnoleFileLoader extends AbstractAnoleLoader{


	public AnoleFileLoader(String environment){
		super(
				environment,
				new FileResourceLoader(),
				new AnoleConfigRegister()
		);
	}



	@Override
	public void load() {
		throw new UnsupportedOperationException("This method is not supported in AnoleFileLoader, please specify at " +
				"least 1 concrete path.");
	}
}
