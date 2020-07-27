package org.tbwork.anole.loader.core.loader.impl;


import org.tbwork.anole.loader.core.parser.AnoleConfigFileParser;
import org.tbwork.anole.loader.core.register.AnoleConfigRegister;
import org.tbwork.anole.loader.core.resource.ResourceLoader;
import org.tbwork.anole.loader.core.resource.impl.FileResourceLoader;

public class AnoleFileLoader extends AbstractAnoleLoader{


	public AnoleFileLoader(String [] includedJarFilters){
		super(
				AnoleConfigFileParser.instance(),
				new FileResourceLoader(includedJarFilters),
				new AnoleConfigRegister()
		);
	}


	public AnoleFileLoader() {
		this(new String[0]);
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException("This method is not supported in AnoleFileLoader, please specify at " +
				"least 1 concrete path.");
	}
}
