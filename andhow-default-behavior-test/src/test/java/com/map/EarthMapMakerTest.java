package com.map;

import org.junit.Test;
import org.yarnandtail.andhow.AndHowTestBase;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.LoaderProblem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author ericeverman
 */
public class EarthMapMakerTest extends AndHowTestBase {

	@Test
	public void testConfigFromPropertiesFileOnly() {
		reloader.reloadDefaultInstance(null);
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("My Map", emm.getMapName());
		assertEquals(-125, emm.getWestBound());
		assertEquals(51, emm.getNorthBound());
		assertEquals(-65, emm.getEastBound());
		assertEquals(23, emm.getSouthBound());
		
	}
	
	@Test
	public void testConfigFromSysProps() {
		
		System.setProperty("com.map.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.map.EarthMapMaker.EAST_BOUND", "-99");
		
		reloader.reloadDefaultInstance(null);
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
	}
	
	@Test
	public void testConfigFromCmdLineThenSysProps() {
		
		System.setProperty("com.map.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.map.EarthMapMaker.EAST_BOUND", "-99");
		
		reloader.reloadDefaultInstance(new String[] {
			"com.map.EarthMapMaker.MAP_NAME=CmdLineMapName",
			"com.map.EarthMapMaker.WEST_BOUND=-179"});
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
		assertEquals(-179, emm.getWestBound());
	}
	
	@Test
	public void testBadInt() {
		
		System.setProperty("com.map.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.map.EarthMapMaker.EAST_BOUND", "East");
		
		try {
			reloader.reloadDefaultInstance(new String[] {
				"com.map.EarthMapMaker.MAP_NAME=CmdLineMapName",
				"com.map.EarthMapMaker.WEST_BOUND=-179"});
		
			fail("Expected an exception");
		} catch (AppFatalException afe) {
			assertEquals(1, afe.getProblems().size());
			assertTrue(afe.getProblems().get(0) instanceof LoaderProblem.StringConversionLoaderProblem);
		}

	}
}
