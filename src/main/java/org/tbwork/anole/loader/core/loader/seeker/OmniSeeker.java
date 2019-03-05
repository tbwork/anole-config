package org.tbwork.anole.loader.core.loader.seeker;

import java.io.InputStream;
import java.util.Map;

/**
 * Seek the directory and find matched files.
 * @author tbwork
 */
public interface OmniSeeker {

	
	/**
	 * Seek files under specified path.
	 * @return a map whose key is the matched file's name and the value is the matched file's content.
	 */
	public Map<String, InputStream> seekFiles();

	
}
