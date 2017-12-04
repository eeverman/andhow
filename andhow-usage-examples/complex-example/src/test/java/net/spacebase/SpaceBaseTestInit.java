package net.spacebase;

import org.yarnandtail.andhow.*;


/**
 * This AndHowInit class will be auto-discovered and used as the source of an
 * AndHowConfiguration instance to configure AndHow during testing.
 * 
 * If there is an <code>AndHowTestInit</code> on the classpath, it will be used 
 * in preference to an <code>AndHowInit</code> class, so its important that a
 * <code>AndHowTestInit</code> class is only ever on the test classpath.
 * 
 */
public class SpaceBaseTestInit implements AndHowTestInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return StdConfig.instance()
				/* Just like SpaceBaseInit, but a slightly different url
				so we can see the change */
				.addFixedValue(PlanetService.LOG_SERVER, "http://test.logserver.com/ps/")
		
				/* More typical for testing:  turn off event broadcast and the cache */
				.addFixedValue(PlanetService.BROADCAST_LOG_EVENTS, false)
				.addFixedValue(PlanetService.ENABLE_CACHE, false);
	}

}
