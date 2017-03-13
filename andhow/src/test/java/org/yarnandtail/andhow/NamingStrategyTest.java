package org.yarnandtail.andhow;

import org.junit.Test;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class NamingStrategyTest {
	


	/**
	 * Test of getUriName method, of class NamingStrategy.
	 */
	@Test
	public void testGetUriName() {
		
		CaseInsensitiveNaming naming = new CaseInsensitiveNaming();
		
		assertEquals("org/cyborg/alfa/rest/ENPOINT_URL", naming.getUriName("org.cyborg.alfa.rest.ENPOINT_URL"));
		assertEquals("ENPOINT_URL", naming.getUriName("ENPOINT_URL"));
		assertEquals("", naming.getUriName(""));	//shouldn't happen
		assertNull(naming.getUriName(null));	//shouldn't happen, unless part of a chain of conversions
	}


	
}
