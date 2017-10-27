package org.dataprocess;

import org.junit.Test;
import org.yarnandtail.andhow.AndHowNonProduction;
import org.yarnandtail.andhow.AndHowTestBase;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class ExternalServiceConnectorTest extends AndHowTestBase {
	
	public ExternalServiceConnectorTest() {
	}
	

	/**
	 * Test of getConnectionUrl method, of class EarthMapMaker.
	 */
	@Test
	public void testAllConfigValues() {
		AndHowNonProduction.builder().build();
		
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals("http://forwardcorp.com/service/", esc.getConnectionUrl());
		assertEquals(60, esc.getConnectionTimeout());
	}
	
}
