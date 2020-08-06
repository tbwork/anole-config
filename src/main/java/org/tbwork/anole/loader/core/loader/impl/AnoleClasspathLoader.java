package org.tbwork.anole.loader.core.loader.impl;

import org.tbwork.anole.loader.core.parser.AnoleConfigFileParser;
import org.tbwork.anole.loader.core.register.AnoleConfigRegister;
import org.tbwork.anole.loader.core.resource.impl.ClasspathResourceLoader;

public class AnoleClasspathLoader extends AbstractAnoleLoader{
	   
	public AnoleClasspathLoader(String environment,
								String [] includeClasspathDirectoryPatterns,
								String [] excludeClasspathDirectoryPatterns){
		super(
				environment,
				new ClasspathResourceLoader(includeClasspathDirectoryPatterns, excludeClasspathDirectoryPatterns),
				new AnoleConfigRegister()
				);
	}
	  
	public AnoleClasspathLoader(String environment){
		this(environment, new String[0], new String[0]);
	}


	@Override
	public void load() {
		load("*.anole");
	}
}
