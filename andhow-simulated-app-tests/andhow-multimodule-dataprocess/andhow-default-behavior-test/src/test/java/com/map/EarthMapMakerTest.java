package com.map;

import org.dataprocess.ExternalServiceConnector;
import org.dataprocess.ExternalServiceConnector.ConnectionConfig;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.AndHowNonProduction;
import org.yarnandtail.andhow.AndHowTestBase;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.load.JndiLoader;
import org.yarnandtail.andhow.util.NameUtil;

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
		
		AndHowNonProduction.builder().build();
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
	}
	
	@Test
	public void testConfigFromCmdLineThenSysProps() {
		
		System.setProperty("com.map.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.map.EarthMapMaker.EAST_BOUND", "-99");
		
		AndHowNonProduction.builder()
				.addCmdLineArgs(new String[] {
					"com.map.EarthMapMaker.MAP_NAME=CmdLineMapName",
					"com.map.EarthMapMaker.WEST_BOUND=-179"})
				.build();
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
		assertEquals(-179, emm.getWestBound());
	}
	
	@Test
	public void testOrderOfLoading() throws Exception {
		
		System.setProperty("com.map.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.map.EarthMapMaker.EAST_BOUND", "-99");
		
		
		SimpleNamingContextBuilder jndi = getJndi();
		jndi.bind("java:" + "com.map.EarthMapMaker.MAP_NAME", "JndiPropMapName");
		jndi.bind("java:" + "com.map.EarthMapMaker.SOUTH_BOUND", "7");
		jndi.bind("java:comp/env/" + "org.dataprocess.ExternalServiceConnector.ConnectionConfig.SERVICE_URL", "test/");
		jndi.activate();
		
		//VALUES IN THE PROPS FILE
		//org.dataprocess.ExternalServiceConnector.ConnectionConfig.SERVICE_URL = http://forwardcorp.com/service/
		//org.dataprocess.ExternalServiceConnector.ConnectionConfig.TIMEOUT = 60
		//com.map.EarthMapMaker.EAST_BOUND = -65
		//com.map.EarthMapMaker.MAP_NAME = My Map
		//com.map.EarthMapMaker.NORTH_BOUND = 51
		//com.map.EarthMapMaker.SOUTH_BOUND = 23
		//com.map.EarthMapMaker.WEST_BOUND = -125
		
		
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals("test/", esc.getConnectionUrl());
		assertEquals(60, esc.getConnectionTimeout());
		
		EarthMapMaker emm = new EarthMapMaker();
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-125, emm.getWestBound());
		assertEquals(51, emm.getNorthBound());
		assertEquals(-99, emm.getEastBound());
		assertEquals(7, emm.getSouthBound());
	}
	
	@Test
	public void testBadInt() {
		
		System.setProperty("com.map.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.map.EarthMapMaker.EAST_BOUND", "East");
		
		try {
			AndHowNonProduction.builder()
					.addCmdLineArgs(new String[] {
						"com.map.EarthMapMaker.MAP_NAME=CmdLineMapName",
						"com.map.EarthMapMaker.WEST_BOUND=-179"})
					.build();
		
			fail("Expected an exception");
		} catch (AppFatalException afe) {
			assertEquals(1, afe.getProblems().size());
			assertTrue(afe.getProblems().get(0) instanceof LoaderProblem.StringConversionLoaderProblem);
		}

	}
}
