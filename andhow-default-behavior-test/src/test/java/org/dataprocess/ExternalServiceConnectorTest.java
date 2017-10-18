/*
 */
package org.dataprocess;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class ExternalServiceConnectorTest {
	
	public ExternalServiceConnectorTest() {
	}
	
	@Before
	public void setUp() {
	}


	/**
	 * Test of getConnectionUrl method, of class ExternalServiceConnector.
	 */
	@Test
	public void testGetConnectionUrl() {
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals("http://forwardcorp.com/service/", esc.getConnectionUrl());
	}

	/**
	 * Test of getConnectionTimeout method, of class ExternalServiceConnector.
	 */
	@Test
	public void testGetConnectionTimeout() {
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals(60, esc.getConnectionTimeout());
	}
	
}
