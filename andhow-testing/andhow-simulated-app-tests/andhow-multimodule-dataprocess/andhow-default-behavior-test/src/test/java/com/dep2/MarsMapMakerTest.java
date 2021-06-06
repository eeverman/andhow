package com.dep2;

import org.dataprocess.ExternalServiceConnector;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.LoaderProblem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author ericeverman
 */
public class MarsMapMakerTest extends AndHowTestBase {

	@Test
	public void testConfigFromPropertiesFileOnly() {
		
		MarsMapMaker mmm = new MarsMapMaker();
		
		//Actual values
		assertEquals("My Map", mmm.getMapName());
		assertEquals(-125, mmm.getWestBound());
		assertEquals(51, mmm.getNorthBound());
		assertEquals(-65, mmm.getEastBound());
		assertEquals(23, mmm.getSouthBound());
		
		assertTrue(mmm.isLogBroadcastEnabled());
		assertEquals("http://prod.mybiz.com.logger/MarsMapMaker/", mmm.getLogServerUrl());
		
	}
	
	@Test
	public void testConfigFromSysProps() {
		
		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "-99");
		
		NonProductionConfig.instance().forceBuild();
		
		MarsMapMaker mmm = new MarsMapMaker();
		
		//Actual values
		assertEquals("SysPropMapName", mmm.getMapName());
		assertEquals(-99, mmm.getEastBound());
	}
	
	@Test
	public void testConfigFromCmdLineThenSysProps() {
		
		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "-99");
		

		NonProductionConfig.instance().setCmdLineArgs(new String[] {
					"com.dep2.MarsMapMaker.MAP_NAME=CmdLineMapName",
					"com.dep2.MarsMapMaker.WEST_BOUND=-179"})
				.forceBuild();
		
		MarsMapMaker mmm = new MarsMapMaker();
		
		//Actual values
		assertEquals("CmdLineMapName", mmm.getMapName());
		assertEquals(-99, mmm.getEastBound());
		assertEquals(-179, mmm.getWestBound());
	}
	
	@Test
	public void testOrderOfLoading() throws Exception {
		
		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "-99");
		
		
		SimpleNamingContextBuilder jndi = getJndi();
		jndi.bind("java:" + "com.dep2.MarsMapMaker.MAP_NAME", "JndiPropMapName");
		jndi.bind("java:" + "com.dep2.MarsMapMaker.SOUTH_BOUND", "7");
		jndi.bind("java:comp/env/" + "org.dataprocess.ExternalServiceConnector.ConnectionConfig.SERVICE_URL", "test/");
		jndi.activate();
		
		//VALUES IN THE PROPS FILE
		//org.dataprocess.ExternalServiceConnector.ConnectionConfig.SERVICE_URL = http://forwardcorp.com/service/
		//org.dataprocess.ExternalServiceConnector.ConnectionConfig.TIMEOUT = 60
		//com.dep2.MarsMapMaker.EAST_BOUND = -65
		//com.dep2.MarsMapMaker.MAP_NAME = My Map
		//com.dep2.MarsMapMaker.NORTH_BOUND = 51
		//com.dep2.MarsMapMaker.SOUTH_BOUND = 23
		//com.dep2.MarsMapMaker.WEST_BOUND = -125
		
		
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals("test/", esc.getConnectionUrl());
		assertEquals(60, esc.getConnectionTimeout());
		
		MarsMapMaker mmm = new MarsMapMaker();
		assertEquals("SysPropMapName", mmm.getMapName());
		assertEquals(-125, mmm.getWestBound());
		assertEquals(51, mmm.getNorthBound());
		assertEquals(-99, mmm.getEastBound());
		assertEquals(7, mmm.getSouthBound());
	}
	
	@Test
	public void testBadInt() {
		
		System.setProperty("com.dep2.MarsMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep2.MarsMapMaker.EAST_BOUND", "East");
		
		try {
			NonProductionConfig.instance()
					.setCmdLineArgs(new String[] {
						"com.dep2.MarsMapMaker.MAP_NAME=CmdLineMapName",
						"com.dep2.MarsMapMaker.WEST_BOUND=-179"})
					.forceBuild();
		
			fail("Expected an exception");
		} catch (AppFatalException afe) {
			assertEquals(1, afe.getProblems().size());
			assertTrue(afe.getProblems().get(0) instanceof LoaderProblem.StringConversionLoaderProblem);
		}

	}
}
