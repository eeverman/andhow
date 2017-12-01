package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.*;

/**
 *
 * @author ericeverman
 */
public class AndHowTestInitB implements AndHowTestInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return StdConfig.instance();
	}

}
