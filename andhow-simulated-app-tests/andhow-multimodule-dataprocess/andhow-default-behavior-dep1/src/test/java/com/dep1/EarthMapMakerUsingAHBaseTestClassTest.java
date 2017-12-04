package com.dep1;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.yarnandtail.andhow.AndHowTestBase;
import org.yarnandtail.andhow.NonProductionConfig;

import static org.junit.Assert.*;

/**
 * Note that these test methods are specified to be executed in Alph sort order
 * to check the impact of the default config verses a forced config.
 * 
 * @author ericeverman
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EarthMapMakerUsingAHBaseTestClassTest extends AndHowTestBase {
	
	public EarthMapMakerUsingAHBaseTestClassTest() {
	}

	
	@Test
	public void testA_ConfigFromSysProps() {

		System.setProperty("EMM.LogServer", "http://localhost.logger/EMM/");
		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");
		
		NonProductionConfig.instance().forceBuild();
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("http://localhost.logger/EMM/", emm.getLogServerUrl());
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
	}
	
	/**
	 * This test should be ordered between the other two tests (see @FixMethodOrder on class)
	 * 
	 * The outer two tests force a custom construction.  This test doesn't not
	 * force configuration to see if the configuration specified by the auto-discovered
	 * TestInitiation class is used.
	 */
	@Test
	public void testB_ZeroConfig() {

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
	
	@Test
	public void testC_ConfigFromCmdLineThenSysProps() {

		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");
		

		NonProductionConfig.instance().setCmdLineArgs(new String[] {
					"com.dep1.EarthMapMaker.MAP_NAME=CmdLineMapName",
					"com.dep1.EarthMapMaker.WEST_BOUND=-179"})
				.forceBuild();
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
		assertEquals(-179, emm.getWestBound());
		
		//default value (was changed in testB
		assertEquals("http://prod.mybiz.com.logger/EarthMapMaker/", emm.getLogServerUrl());
	}

	
}
