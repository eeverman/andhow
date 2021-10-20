package org.yarnandtail.andhow.valuetype;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import static org.junit.jupiter.api.Assertions.*;

public class StrTypeTest {

	@Test
	public void testInstance() {
		StrType t1 = StrType.instance();
		StrType t2 = StrType.instance();
		assertNotNull(t1);
		assertSame(t1, t2);
	}

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
