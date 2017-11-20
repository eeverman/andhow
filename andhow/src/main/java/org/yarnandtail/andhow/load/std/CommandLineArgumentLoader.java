package org.yarnandtail.andhow.load.std;

import java.util.List;
import org.yarnandtail.andhow.load.KeyValuePairLoader;

/**
 * Identical to the KeyValuePairLoader, but provides a way to find
 the cmd line arg loader in a list of loaders.  There should only be a single
 * one, auto added, where as someone may choose to add several StringArgumentLaoders.
 * 
 * @author ericeverman
 */
public class CommandLineArgumentLoader extends KeyValuePairLoader {

	public CommandLineArgumentLoader() {
		unknownPropertyAProblem = false;
	}

}
