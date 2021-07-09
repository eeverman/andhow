package org.dataprocess;

import org.yarnandtail.andhow.AndHowJunit5TestBase;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The EarthMapMakerTest has more complete examples of testing.
 *
 * @author ericeverman
 */
public class ExternalServiceConnectorTest extends AndHowJunit5TestBase {
	
	public ExternalServiceConnectorTest() {
	}

	@Test
	public void testAllConfigValues() {
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals("http://forwardcorp.com/service/", esc.getConnectionUrl());
		assertEquals(60, esc.getConnectionTimeout());
	}
	
}
