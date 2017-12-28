package com.dep2;

import org.yarnandtail.andhow.*;

import static com.dep2.MarsMapMaker.BROADCAST_LOG_EVENTS;
import static com.dep2.MarsMapMaker.LOG_SERVER;
import static com.dep2.MarsMapMaker.MAP_NAME;

/**
 *
 * @author ericeverman
 */
public class TestInitiation implements AndHowTestInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return StdConfig.instance()
				.addFixedValue(MAP_NAME, "Mars Test Map")
				.addFixedValue(BROADCAST_LOG_EVENTS, false)
				.addFixedValue(LOG_SERVER, "http://dev.mybiz.com.logger/MarsMapMaker/");
	}

}
