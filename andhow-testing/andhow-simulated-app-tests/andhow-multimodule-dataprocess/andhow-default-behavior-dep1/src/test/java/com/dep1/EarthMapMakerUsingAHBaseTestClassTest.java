package com.dep1;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import static org.junit.jupiter.api.Assertions.*;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.AndHowConfiguration;
import org.yarnandtail.andhow.StdConfig;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeEachTest;
import org.yarnandtail.andhow.junit5.RestoreSysPropsAfterEachTest;


/**
 * Test methods are executed in Alpha sort order.
 * 
 * @author ericeverman
 */
@KillAndHowBeforeEachTest
@RestoreSysPropsAfterEachTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class EarthMapMakerUsingAHBaseTestClassTest {
	
	@Test	//ordered
	public void test1_ConfigFromSysProps() {

		System.setProperty("EMM.LogServer", "http://localhost.logger/EMM/");
		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");

		//Bypass config discovery of {@link TestInitiation} and set a vanilla config, which will
		//see the Sys Props from above.
		AndHow.setConfig(StdConfig.instance());
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("http://localhost.logger/EMM/", emm.getLogServerUrl());
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
	}
	
	/**
	 * This test happens in order after test1 & before test 3.
	 * 
	 * The outer two tests force a custom construction, but this test should see the auto-discovered
	 * TestInitiation configuration.
	 */
	@Test	//ordered
	public void test2_ZeroConfig() {

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
	
	@Test	//ordered
	public void test3_ConfigFromCmdLineThenSysProps() {

		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");

		AndHowConfiguration config = StdConfig.instance().setCmdLineArgs(new String[] {
					"com.dep1.EarthMapMaker.MAP_NAME=CmdLineMapName",
					"com.dep1.EarthMapMaker.WEST_BOUND=-179"});

		//Bypass config discovery of {@link TestInitiation} and set a vanilla config, which will
		//see the Sys Props and cmdline args from above.
		AndHow.setConfig(config);
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
		assertEquals(-179, emm.getWestBound());
		
		//default value (was changed in testB
		assertEquals("http://prod.mybiz.com.logger/EarthMapMaker/", emm.getLogServerUrl());
	}
}
