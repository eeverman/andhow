package com.dep1;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class EarthMapMakerNotUsingAHBaseTestClassTest {
	
	public EarthMapMakerNotUsingAHBaseTestClassTest() {
	}

	/**
	 * Test of makeMap method, of class EarthMapMaker.
	 */
	@Test
	public void testZeroConfig() {
		EarthMapMaker emm = new EarthMapMaker();
		
		//These values set in the TestInitiation (discovered automatically)
		assertFalse(emm.isLogBroadcastEnabled());
		assertEquals("http://dev.mybiz.com.logger/EarthMapMaker/", emm.getLogServerUrl());
		assertEquals("Earth Test Map", emm.getMapName());
		
		//These are all just defaults
		
		assertEquals(-124, emm.getWestBound());
		assertEquals(50, emm.getNorthBound());
		assertEquals(-66, emm.getEastBound());
		assertEquals(24, emm.getSouthBound());
	}

	
}
