/*
 */
package org.yarnandtail.andhow.valuetype;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import static org.junit.jupiter.api.Assertions.*;

public class BolTypeTest {

	@Test
	public void testInstance() {
		BolType t1 = BolType.instance();
		BolType t2 = BolType.instance();
		assertNotNull(t1);
		assertSame(t1, t2);
	}

	@Test
	public void testParseHappyPath() throws ParsingException {

		BolType type = BolType.instance();

		// True values (these are canonically true according to AndHow)
		assertTrue(type.parse("true"));
		assertTrue(type.parse("t"));
		assertTrue(type.parse("yes"));
		assertTrue(type.parse("y"));
		assertTrue(type.parse("on"));

		// Extra 'true' values - it turns out the BolType uses the TextUtil.toBoolean
		// method, which is forgiving in that it trims whitespace.  This is really a
		// double trim, since the loader should trim these prior to giving to the parse method.
		assertTrue(type.parse(" true "));
		assertTrue(type.parse(" yes"));

		// False values
		assertFalse(type.parse(""));
		assertFalse(type.parse(" anything "));
		assertFalse(type.parse("NoWhitespaceIsAlsoFalseIfNotRecognized"));

		// Null is unset
		assertNull(type.parse(null));
	}

	@Test
	public void testCast() {

		BolType type = BolType.instance();

		Object o = Boolean.valueOf(true);
		assertEquals(Boolean.TRUE, type.cast(o));
		assertTrue(type.cast(o) instanceof Boolean);
	}

}
