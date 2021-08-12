package com.dep2;

import org.dataprocess.ExternalServiceConnector;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.LoaderProblem;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeThisTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class MarsMapMakerTest {

	/**
	 * Its OK to do nothing.  AndHow will initialize itself at the point
	 * of the first Property value access.
	 *
	 * AndHowJunit5TestBase allows you to modify AndHow's state
	 * for each test class and/or for each test method - something
	 * that is NOT possible in production.
	 * If you use the AndHowJunit5TestBase for your tests, any changes
	 * you make to AndHow's state in a test class or during a test
	 * method are reverted after each method / test class completes.
	 */
	@Test
	@KillAndHowBeforeThisTest
	public void testConfigFromPropertiesFileOnly() {
		
		MarsMapMaker mmm = new MarsMapMaker();

		//These values read from andhow.properties - see the EarthMapMakerTest for
		//more details.
		assertEquals("My Map", mmm.getMapName());	//AndHow initializes here if it hasn't already.
		assertEquals(-125, mmm.getWestBound());
		assertEquals(51, mmm.getNorthBound());
		assertEquals(-65, mmm.getEastBound());
		assertEquals(23, mmm.getSouthBound());
		
		assertTrue(mmm.isLogBroadcastEnabled());
		assertEquals("http://prod.mybiz.com.logger/MarsMapMaker/", mmm.getLogServerUrl());
		
	}

}
