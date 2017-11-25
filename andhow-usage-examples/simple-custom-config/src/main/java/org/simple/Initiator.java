package org.simple;

import org.yarnandtail.andhow.*;

/**
 *
 * @author ericeverman
 */
public class Initiator implements AndHowInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return  StdConfig.instance().setClasspathPropFilePath("/simple.properties");
	}

}
