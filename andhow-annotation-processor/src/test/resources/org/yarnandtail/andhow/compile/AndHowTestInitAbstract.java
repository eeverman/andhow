package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.*;

/**
 *
 * @author ericeverman
 */
public abstract class AndHowTestInitAbstract implements AndHowTestInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return StdConfig.instance();
	}

}
