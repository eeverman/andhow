package com.map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author ericeverman
 */
public class EarthMapMakerTest {

	@Test
	public void testAllConfigValues() {
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("My Map", emm.getMapName());
		assertEquals(-125, emm.getWestBound());
		assertEquals(51, emm.getNorthBound());
		assertEquals(-65, emm.getEastBound());
		assertEquals(23, emm.getSouthBound());
		
	}
}
