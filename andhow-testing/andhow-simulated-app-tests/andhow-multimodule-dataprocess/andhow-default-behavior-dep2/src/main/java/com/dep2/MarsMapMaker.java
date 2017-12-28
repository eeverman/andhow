package com.dep2;

import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.*;

/**
 * 
 * @author eeverman
 */
@GroupInfo(name="Configuration for Mars map generation", desc="Bounds define max map extents")
public class MarsMapMaker {
	public static final StrProp MAP_NAME = StrProp.builder().desc("Name displayed at the top of the map").mustBeNonNull().build();
	private static final IntProp WEST_BOUND = IntProp.builder().defaultValue(-124).desc("West-most edge of map, in deg. longitude").mustBeNonNull().build();
	private static final IntProp NORTH_BOUND = IntProp.builder().defaultValue(50).desc("North-most edge of map, in deg. latitue").mustBeNonNull().build();
	private static final IntProp EAST_BOUND = IntProp.builder().defaultValue(-66).desc("East-most edge of map, in deg. longitude").mustBeNonNull().build();
	private static final IntProp SOUTH_BOUND = IntProp.builder().defaultValue(24).desc("South-most edge of map, in deg. latitue").mustBeNonNull().build();
	
	//System logging configuration for this class
	public static final BolProp BROADCAST_LOG_EVENTS = BolProp.builder().aliasIn("MMM.Broadcast").defaultValue(true)
			.desc("If true, logs events are sent to the central logging server").build();
	public static final StrProp LOG_SERVER = StrProp.builder().aliasIn("MMM.LogServer")
			.mustStartWith("http://").mustEndWith("/")
			.defaultValue("http://prod.mybiz.com.logger/MarsMapMaker/")
			.desc("The logging server to send events to").build();	
	
	public String makeMap() {
		return "Make a map here...";
	}

	public String getMapName() {
		return MAP_NAME.getValue();
	}

	public int getWestBound() {
		return WEST_BOUND.getValue();
	}

	public int getNorthBound() {
		return NORTH_BOUND.getValue();
	}

	public int getEastBound() {
		return EAST_BOUND.getValue();
	}

	public int getSouthBound() {
		return SOUTH_BOUND.getValue();
	}
	
	public boolean isLogBroadcastEnabled() {
		return BROADCAST_LOG_EVENTS.getValue();
	}

	public String getLogServerUrl() {
		return LOG_SERVER.getValue();
	}
	
}
