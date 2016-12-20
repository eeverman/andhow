/*
 */
package yarnandtail.andhow.valuetype;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class QuotedStringTypeTest {
	
	/**
	 * Test of convert method, of class QuotedStringType.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testConvert() throws Exception {
		QuotedStringType qst = QuotedStringType.instance();
		
		assertEquals("abc", qst.convert("   abc   "));
		assertEquals("   abc   ", qst.convert("\"   abc   \""));
		assertEquals("   \"a\"bc   ", qst.convert("\"   \"a\"bc   \""));
		assertEquals("\"a\"bc", qst.convert("\"a\"bc"));
	}

	/**
	 * Test of cast method, of class QuotedStringType.
	 */
	@Test
	public void testCast() {
		QuotedStringType qst = QuotedStringType.instance();
		Object o = "abc";
		assertEquals("abc", qst.cast(o));
		assertTrue(qst.cast(o) instanceof String);
	}
	
}
