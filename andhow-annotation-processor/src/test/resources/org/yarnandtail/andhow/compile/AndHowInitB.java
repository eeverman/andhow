package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.*;

/**
 *
 * @author ericeverman
 */
public class AndHowInitB implements AndHowInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return StdConfig.instance();
	}

}
