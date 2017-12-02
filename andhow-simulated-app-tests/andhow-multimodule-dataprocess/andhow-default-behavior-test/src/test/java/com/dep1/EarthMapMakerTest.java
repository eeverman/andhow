package com.dep1;

import org.dataprocess.ExternalServiceConnector;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.*;
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
		
		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");
		
		NonProductionConfig.instance().forceBuild();
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("SysPropMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
	}
	
	@Test
	public void testConfigFromCmdLineThenSysProps() {
		
		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");
		

		NonProductionConfig.instance().addCmdLineArgs(new String[] {
					"com.dep1.EarthMapMaker.MAP_NAME=CmdLineMapName",
					"com.dep1.EarthMapMaker.WEST_BOUND=-179"})
				.forceBuild();
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", emm.getMapName());
		assertEquals(-99, emm.getEastBound());
		assertEquals(-179, emm.getWestBound());
	}
	
	@Test
	public void testOrderOfLoading() throws Exception {
		
		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");
		
		
		SimpleNamingContextBuilder jndi = getJndi();
		jndi.bind("java:" + "com.dep1.EarthMapMaker.MAP_NAME", "JndiPropMapName");
		jndi.bind("java:" + "com.dep1.EarthMapMaker.SOUTH_BOUND", "7");
		jndi.bind("java:comp/env/" + "org.dataprocess.ExternalServiceConnector.ConnectionConfig.SERVICE_URL", "test/");
		jndi.activate();
		
		//VALUES IN THE PROPS FILE
		//org.dataprocess.ExternalServiceConnector.ConnectionConfig.SERVICE_URL = http://forwardcorp.com/service/
		//org.dataprocess.ExternalServiceConnector.ConnectionConfig.TIMEOUT = 60
		//com.dep1.EarthMapMaker.EAST_BOUND = -65
		//com.dep1.EarthMapMaker.MAP_NAME = My Map
		//com.dep1.EarthMapMaker.NORTH_BOUND = 51
		//com.dep1.EarthMapMaker.SOUTH_BOUND = 23
		//com.dep1.EarthMapMaker.WEST_BOUND = -125
		
		
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
		
		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "East");
		
		try {
			NonProductionConfig.instance()
					.addCmdLineArgs(new String[] {
						"com.dep1.EarthMapMaker.MAP_NAME=CmdLineMapName",
						"com.dep1.EarthMapMaker.WEST_BOUND=-179"})
					.forceBuild();
		
			fail("Expected an exception");
		} catch (AppFatalException afe) {
			assertEquals(1, afe.getProblems().size());
			assertTrue(afe.getProblems().get(0) instanceof LoaderProblem.StringConversionLoaderProblem);
		}

	}
}
