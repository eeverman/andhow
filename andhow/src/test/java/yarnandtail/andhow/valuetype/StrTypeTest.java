/*
 */
package yarnandtail.andhow.valuetype;

import org.junit.Test;
import static org.junit.Assert.*;
import yarnandtail.andhow.ParsingException;

/**
 *
 * @author ericeverman
 */
public class StrTypeTest {
	

	@Test
	public void testParseHappyPath() throws ParsingException {
		
		StrType type = StrType.instance();
		
		assertEquals("-1234", type.parse("-1234"));
		assertEquals("  apple  ", type.parse("  apple  "));
		assertEquals(" ", type.parse(" "));
		assertEquals("", type.parse(""));
		assertNull(type.parse(null));
	}
	
	@Test
	public void testCast() {
		StrType qst = StrType.instance();
		Object o = "abc";
		assertEquals("abc", qst.cast(o));
		assertTrue(qst.cast(o) instanceof String);
	}
	
}
