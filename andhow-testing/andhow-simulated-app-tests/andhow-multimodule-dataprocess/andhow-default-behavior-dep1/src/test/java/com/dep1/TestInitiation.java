package com.dep1;

import org.yarnandtail.andhow.*;

import static com.dep1.EarthMapMaker.BROADCAST_LOG_EVENTS;
import static com.dep1.EarthMapMaker.LOG_SERVER;
import static com.dep1.EarthMapMaker.MAP_NAME;

/**
 *
 * @author ericeverman
 */
public class TestInitiation implements AndHowTestInit {

	@Override
	public AndHowConfiguration getConfiguration() {
		return AndHow.findConfig()
				.addFixedValue(MAP_NAME, "Earth Test Map")
				.addFixedValue(BROADCAST_LOG_EVENTS, false)
				.addFixedValue(LOG_SERVER, "http://dev.mybiz.com.logger/EarthMapMaker/");
	}

}
