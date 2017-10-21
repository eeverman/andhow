package org.yarnandtail.andhow.load;

import java.util.List;

/**
 * Identical to the StringArgumentLoader, but provides a way to find
 * the cmd line arg loader in a list of loaders.  There should only be a single
 * one, auto added, where as someone may choose to add several StringArgumentLaoders.
 * 
 * @author ericeverman
 */
public class CommandLineArgumentLoader extends StringArgumentLoader {

	public CommandLineArgumentLoader(List<String> inCmdLineArgs) {
		super(inCmdLineArgs);
	}
	
	public CommandLineArgumentLoader(String... inCmdLineArgs) {
		super(inCmdLineArgs);
	}

}
