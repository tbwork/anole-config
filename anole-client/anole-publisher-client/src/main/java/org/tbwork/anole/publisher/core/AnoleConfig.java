package org.tbwork.anole.publisher.core;

import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.publisher.model.ConfigChangeRequest;

/**
 * Same name to AnoleConfig in anole-subscriber-client.
 * And a reasonable project will never reference the two
 * packages at the same time.
 * @author tommy.tang
 */
public class AnoleConfig extends AnoleLocalConfig{
	public static void set(ConfigChangeRequest ccr){
		
	}
}
