package org.tbwork.anole.loader.core.loader.impl;

import org.tbwork.anole.loader.core.parser.AnoleConfigFileParser;
import org.tbwork.anole.loader.core.register.AnoleConfigRegister;
import org.tbwork.anole.loader.core.resource.impl.ClasspathResourceLoader;

public class AnoleClasspathLoader extends AbstractAnoleLoader{
	   
	public AnoleClasspathLoader(String [] includeClasspathDirectoryPatterns,
								String [] excludeClasspathDirectoryPatterns){
		super(
				AnoleConfigFileParser.instance(),
				new ClasspathResourceLoader(includeClasspathDirectoryPatterns, excludeClasspathDirectoryPatterns),
				new AnoleConfigRegister()
				);
	}
	  
	public AnoleClasspathLoader(){
		this(new String[0], new String[0]);
	}


	@Override
	public void load() {
		load("*.anole");
	}
}
