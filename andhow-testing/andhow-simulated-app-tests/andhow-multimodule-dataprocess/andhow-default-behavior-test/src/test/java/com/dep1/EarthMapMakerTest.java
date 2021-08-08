package com.dep1;

import org.dataprocess.ExternalServiceConnector;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.AppFatalException;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.junit5.RestoreSysPropsAfterThisTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class EarthMapMakerTest extends AndHowJunit5TestBase {

	/**
	 * This test will see the properties set in the andhow.properties file on the classpath.
	 * Currently that file is at: <code>/src/main/resources/andhow.properties</code></br>
	 * Optionally, an set of properties used only during testing could be placed at:
	 * <code>/src/test/resources/andhow.properties</code></br>
	 * That file would then be used exclusively during testing.
	 */
	@Test
	public void testConfigFromPropertiesFileOnly() {
		
		EarthMapMaker emm = new EarthMapMaker();
		
		//These values match what is loaded from the andhow.properties file
		assertEquals("My Map", emm.getMapName());
		assertEquals(-125, emm.getWestBound());
		assertEquals(51, emm.getNorthBound());
		assertEquals(-65, emm.getEastBound());
		assertEquals(23, emm.getSouthBound());
		
		assertTrue(emm.isLogBroadcastEnabled());
		assertEquals("http://prod.mybiz.com.logger/EarthMapMaker/", emm.getLogServerUrl());
		
	}

	/**
	 * Sometimes a test will need custom configuration, such as wanting to
	 * test your app when it is configured a particular way.
	 * This example does that for just this one method.
	 *
	 * By using the base class <code>AndHowJunit5TestBase</code>, AndHow
	 * configuration will automatically revert to its original state
	 * (configured via the property file <code>andhow.properties</code>)
	 * when this method completes.
	 */
	@Test
	public void testUsingCustomConfiguration() {

		NonProductionConfig.instance()
				.addFixedValue(EarthMapMaker.MAP_NAME, "SomeNameILike") /* via Property reference */
				.addFixedValue("com.dep1.EarthMapMaker.EAST_BOUND", 42) /* via Property name */
				.forceBuild();

		EarthMapMaker emm = new EarthMapMaker();

		//These values were customized above
		assertEquals("SomeNameILike", emm.getMapName());
		assertEquals(42, emm.getEastBound());

		//All other values still come from the andhow.properties file
		assertEquals(51, emm.getNorthBound());
	}


	/**
	 * This just demonstrates the order of loading.
	 * The System Properties Loader is earlier in the loader list than the
	 * JNDI loader, thus, values set as System Properties 'win' and override
	 * values found in JNDI.  Properties files are read last of all.
	 *
	 * @throws Exception
	 */
	@Test
	@RestoreSysPropsAfterThisTest
	public void testOrderOfLoading() throws Exception {
		
		System.setProperty("com.dep1.EarthMapMaker.MAP_NAME", "SysPropMapName");
		System.setProperty("com.dep1.EarthMapMaker.EAST_BOUND", "-99");
		
		
		SimpleNamingContextBuilder jndi = getJndi();
		jndi.bind("java:" + "com.dep1.EarthMapMaker.MAP_NAME", "JndiPropMapName");
		jndi.bind("java:" + "com.dep1.EarthMapMaker.SOUTH_BOUND", "7");
		jndi.bind("java:comp/env/" + "org.dataprocess.ExternalServiceConnector.ConnectionConfig.SERVICE_URL", "test/");
		jndi.activate();

		NonProductionConfig.instance().forceBuild();
		
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

	/**
	 * Validation is always enforced
	 */
	@Test
	public void testAttemptToAssignAnInvalidValue() {

		//LOG_SERVER must start w/ "http://"
		assertThrows(AppFatalException.class, () ->{
			NonProductionConfig.instance()
					.addFixedValue("com.dep1.EarthMapMaker.LOG_SERVER", "ftp://blah/")
					.forceBuild();
		});

	}
}
