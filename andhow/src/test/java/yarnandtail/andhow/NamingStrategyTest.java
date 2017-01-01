package yarnandtail.andhow;

import org.junit.Test;
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
		assertEquals("org/cyborg/alfa/rest/ENPOINT_URL", NamingStrategy.getUriName("org.cyborg.alfa.rest.ENPOINT_URL"));
		assertEquals("ENPOINT_URL", NamingStrategy.getUriName("ENPOINT_URL"));
		assertEquals("", NamingStrategy.getUriName(""));	//shouldn't happen
		assertNull(NamingStrategy.getUriName(null));	//shouldn't happen, unless part of a chain of conversions
	}


	
}
