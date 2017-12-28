package com.dep2;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class MarsMapMakerNotUsingAHBaseTestClassTest {
	

	/**
	 * Test of makeMap method, of class EarthMapMaker.
	 */
	@Test
	public void testZeroConfig() {
		MarsMapMaker emm = new MarsMapMaker();
		
		//These values set in the TestInitiation (discovered automatically)
		assertFalse(emm.isLogBroadcastEnabled());
		assertEquals("http://dev.mybiz.com.logger/MarsMapMaker/", emm.getLogServerUrl());
		assertEquals("Mars Test Map", emm.getMapName());
		
		//These are all just defaults
		assertEquals(-124, emm.getWestBound());
		assertEquals(50, emm.getNorthBound());
		assertEquals(-66, emm.getEastBound());
		assertEquals(24, emm.getSouthBound());
	}

	
}
