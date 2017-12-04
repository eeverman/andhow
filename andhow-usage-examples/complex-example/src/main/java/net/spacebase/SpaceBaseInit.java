package net.spacebase;

import org.yarnandtail.andhow.*;


/**
 * This AndHowInit class will be auto-discovered and used as the source of an
 * AndHowConfiguration instance to configure AndHow at application startup.
 * 
 * If there is an <code>AndHowTestInit</code> on the classpath, it will be used 
 * in preference to an <code>AndHowInit</code> class, so its important that a
 * <code>AndHowTestInit</code> class is only ever on the test classpath.
 * 
 */
public class SpaceBaseInit implements AndHowInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return StdConfig.instance()
				/* force a specific logging server via a fixed value in the 
				configuration - this would not be a best practice, but is an example
				of how values can be forced in configuration. */
				.addFixedValue(PlanetService.LOG_SERVER, "http://prod.logserver.com/ps/");
	}

}
