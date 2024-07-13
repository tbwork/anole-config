package com.github.tbwork.anole.loader.core.loader.impl;

import com.github.tbwork.anole.loader.core.register.AnoleConfigRegister;
import com.github.tbwork.anole.loader.core.resource.impl.ClasspathResourceLoader;

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
