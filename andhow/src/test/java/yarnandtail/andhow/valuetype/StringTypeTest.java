/*
 */
package yarnandtail.andhow.valuetype;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class StringTypeTest {
	

	/**
	 * Test of cast method, of class QuotedStringType.
	 */
	@Test
	public void testCast() {
		StringType qst = StringType.instance();
		Object o = "abc";
		assertEquals("abc", qst.cast(o));
		assertTrue(qst.cast(o) instanceof String);
	}
	
}
