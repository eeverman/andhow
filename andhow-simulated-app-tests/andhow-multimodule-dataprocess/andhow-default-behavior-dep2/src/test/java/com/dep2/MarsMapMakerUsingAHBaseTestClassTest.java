package com.dep2;

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
public class MarsMapMakerUsingAHBaseTestClassTest extends AndHowTestBase {
	
	public MarsMapMakerUsingAHBaseTestClassTest() {
	}

	
	@Test
	public void testA_ConfigFromSysProps() {
		System.out.println("testConfigFromSysProps");
		
		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "-99");
		
		NonProductionConfig.instance().forceBuild();
		
		MarsMapMaker emm = new MarsMapMaker();
		
		//Actual values
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
	}
	
	@Test
	public void testB_ZeroConfig() {
		System.out.println("testZeroConfig");
		
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
	
	@Test
	public void testC_ConfigFromCmdLineThenSysProps() {
		System.out.println("testConfigFromCmdLineThenSysProps");
		
		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "-99");
		

		NonProductionConfig.instance().addCmdLineArgs(new String[] {
					"com.dep2.MarsMapMaker.MAP_NAME=CmdLineMapName",
					"com.dep2.MarsMapMaker.WEST_BOUND=-179"})
				.forceBuild();
		
		MarsMapMaker emm = new MarsMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
		assertEquals(-179, emm.getWestBound());
		
		//default value (was changed in testB
		assertEquals("http://prod.mybiz.com.logger/MarsMapMaker/", emm.getLogServerUrl());
	}

	
}
