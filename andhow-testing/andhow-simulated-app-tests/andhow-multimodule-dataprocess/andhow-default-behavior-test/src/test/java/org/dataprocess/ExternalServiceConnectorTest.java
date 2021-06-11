package org.dataprocess;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHowTestBase;

import static org.junit.jupiter.api.Assertions.*;

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
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals("http://forwardcorp.com/service/", esc.getConnectionUrl());
		assertEquals(60, esc.getConnectionTimeout());
	}
	
}
