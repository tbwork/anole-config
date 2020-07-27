package org.tbwork.anole.loader.core.loader.impl;
 
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.parser.AnoleConfigFileParser;
import org.tbwork.anole.loader.core.register.AnoleConfigRegister;
import org.tbwork.anole.loader.core.resource.impl.ClasspathResourceLoader;
import org.tbwork.anole.loader.core.resource.impl.FileResourceLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.CollectionUtil;
import org.tbwork.anole.loader.util.FileUtil;
import org.tbwork.anole.loader.util.ProjectUtil;

import java.util.*;

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
