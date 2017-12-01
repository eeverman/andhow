package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.*;

/**
 *
 * @author ericeverman
 */
public abstract class AndHowInitAbstract implements AndHowInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return StdConfig.instance();
	}

}
