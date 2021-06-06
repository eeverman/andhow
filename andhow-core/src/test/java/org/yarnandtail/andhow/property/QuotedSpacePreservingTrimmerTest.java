/*
 */
package org.yarnandtail.andhow.property;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author ericeverman
 */
public class QuotedSpacePreservingTrimmerTest {
	
	
	@Test
	public void testTrim() throws Exception {
		QuotedSpacePreservingTrimmer trimmer = new QuotedSpacePreservingTrimmer();
		
		assertEquals("abc", trimmer.trim("   abc   "));
		assertEquals("   abc   ", trimmer.trim("\"   abc   \""));
		assertEquals("   \"a\"bc   ", trimmer.trim("\"   \"a\"bc   \""));
		assertEquals("\"a\"bc", trimmer.trim("\"a\"bc"));
		assertEquals("", trimmer.trim("\"\""));
		
		//to null
		assertNull(trimmer.trim(""));
		assertNull(trimmer.trim("  \t\n  "));
		assertNull(trimmer.trim(null));
	}


	
}
