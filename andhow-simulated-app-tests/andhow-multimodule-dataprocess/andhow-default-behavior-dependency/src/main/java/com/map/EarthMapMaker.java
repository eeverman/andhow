package com.map;

import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.*;

/**
 * 
 * @author eeverman
 */
@GroupInfo(name="Configuration for map generation", desc="Bounds define max map extents")
public class EarthMapMaker {
	private static final StrProp MAP_NAME = StrProp.builder().desc("Name displayed at the top of the map").mustBeNonNull().build();
	private static final IntProp WEST_BOUND = IntProp.builder().defaultValue(-124).desc("West-most edge of map, in deg. longitude").mustBeNonNull().build();
	private static final IntProp NORTH_BOUND = IntProp.builder().defaultValue(50).desc("North-most edge of map, in deg. latitue").mustBeNonNull().build();
	private static final IntProp EAST_BOUND = IntProp.builder().defaultValue(-66).desc("East-most edge of map, in deg. longitude").mustBeNonNull().build();
	private static final IntProp SOUTH_BOUND = IntProp.builder().defaultValue(24).desc("South-most edge of map, in deg. latitue").mustBeNonNull().build();
	
	public String fetchData() {
		return "Got some data...";
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
	

	
}
