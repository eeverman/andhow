package org.dataprocess;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeThisTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The EarthMapMakerTest has more complete examples of testing.
 *
 * @author ericeverman
 */
public class ExternalServiceConnectorTest {

	@Test
	@KillAndHowBeforeThisTest
	public void testAllConfigValues() {
		ExternalServiceConnector esc = new ExternalServiceConnector();
		assertEquals("http://forwardcorp.com/service/", esc.getConnectionUrl());
		assertEquals(60, esc.getConnectionTimeout());
	}
	
}
