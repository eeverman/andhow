package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.api.Name;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author eeverman
 */
public class NameTest {
	

	@Test
	public void testIsValidPropertyAlias() {
		
		//OKs
		assertTrue(Name.isValidPropertyName("a"));
		assertTrue(Name.isValidPropertyName("-a"));
		assertTrue(Name.isValidPropertyName("_a.a_"));
		assertTrue(Name.isValidPropertyName("a-.-a"));
		assertTrue(Name.isValidPropertyName("a-.-a"));
		
		//not alloweds
		assertFalse(Name.isValidPropertyName(null));
		assertFalse(Name.isValidPropertyName(""));
		assertFalse(Name.isValidPropertyName("\t"));
		assertFalse(Name.isValidPropertyName(" "));
		assertFalse(Name.isValidPropertyName(" surroundedBySpace "));
		assertFalse(Name.isValidPropertyName("contains space"));
		assertFalse(Name.isValidPropertyName(".startsWithDot"));
		assertFalse(Name.isValidPropertyName("endsWithDot."));
		assertFalse(Name.isValidPropertyName("no=equals"));
		assertFalse(Name.isValidPropertyName("no?question"));
		assertFalse(Name.isValidPropertyName("no/forward/slash"));
	}
			
	
}
