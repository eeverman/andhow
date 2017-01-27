package yarnandtail.andhow;

import org.junit.Test;
import yarnandtail.andhow.name.BasicNamingStrategy;

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
		
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		assertEquals("org/cyborg/alfa/rest/ENPOINT_URL", bns.getUriName("org.cyborg.alfa.rest.ENPOINT_URL"));
		assertEquals("ENPOINT_URL", bns.getUriName("ENPOINT_URL"));
		assertEquals("", bns.getUriName(""));	//shouldn't happen
		assertNull(bns.getUriName(null));	//shouldn't happen, unless part of a chain of conversions
	}


	
}
