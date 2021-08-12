package com.dep2;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import static org.junit.jupiter.api.Assertions.*;

import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeEachTest;
import org.yarnandtail.andhow.junit5.RestoreSysPropsAfterEachTest;

/**
 * Note that these test methods are specified to be executed in Alph sort order
 * to check the impact of the default config verses a forced config.
 * 
 * @author ericeverman
 */
@KillAndHowBeforeEachTest
@RestoreSysPropsAfterEachTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class MarsMapMakerUsingAHBaseTestClassTest {

	@Test
	public void test1_ConfigFromSysProps() {

		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "-99");

		//Bypass config discovery of {@link TestInitiation} and set a vanilla config, which will
		//see the Sys Props from above.
		AndHow.setConfig(StdConfig.instance());
		
		MarsMapMaker emm = new MarsMapMaker();
		
		//Actual values
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
	}

	/**
	 * This test happens in order after test1 & before test 3.
	 *
	 * The outer two tests force a custom construction, but this test should see the auto-discovered
	 * TestInitiation configuration.
	 */
	@Test
	public void test2_ZeroConfig() {
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
	public void test3_ConfigFromCmdLineThenSysProps() {
		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "-99");


		AndHowConfiguration config = StdConfig.instance().setCmdLineArgs(new String[] {
					"com.dep2.MarsMapMaker.MAP_NAME=CmdLineMapName",
					"com.dep2.MarsMapMaker.WEST_BOUND=-179"});

		//Bypass config discovery of {@link TestInitiation} and set a vanilla config, which will
		//see the Sys Props and cmdline args from above.
		AndHow.setConfig(config);
		
		MarsMapMaker emm = new MarsMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
		assertEquals(-179, emm.getWestBound());
		
		//default value (was changed in testB
		assertEquals("http://prod.mybiz.com.logger/MarsMapMaker/", emm.getLogServerUrl());
	}

	
}
