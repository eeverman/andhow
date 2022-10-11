package org.yarnandtail.andhow.valuetype;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import static org.junit.jupiter.api.Assertions.*;

class FlagTypeTest {

	@Test
	void instance() {
		FlagType ft1 = FlagType.instance();
		FlagType ft2 = FlagType.instance();
		FlagType ft3 = new FlagType();

		assertNotNull(ft1);
		assertSame(ft1, ft2);
		assertNotSame(ft1, ft3);
	}

	@Test
	public void testParseAsFlaggableType() throws ParsingException {

		FlagType type = FlagType.instance();

		// True values (these are canonically true according to AndHow)
		assertTrue(type.parseFlag("true"));
		assertTrue(type.parseFlag("t"));
		assertTrue(type.parseFlag("yes"));
		assertTrue(type.parseFlag("y"));
		assertTrue(type.parseFlag("on"));

		// True values (uppercase is OK)
		assertTrue(type.parseFlag("TRUE"));
		assertTrue(type.parseFlag("TrUe"));
		assertTrue(type.parseFlag("T"));
		assertTrue(type.parseFlag("YES"));
		assertTrue(type.parseFlag("Y"));
		assertTrue(type.parseFlag("ON"));

		// False values (canonically false according to AndHow)
		assertFalse(type.parseFlag("false"));
		assertFalse(type.parseFlag("f"));
		assertFalse(type.parseFlag("no"));
		assertFalse(type.parseFlag("n"));
		assertFalse(type.parseFlag("off"));

		// False values (UPPERCASE is OK)
		assertFalse(type.parseFlag("FALSE"));
		assertFalse(type.parseFlag("FaLsE"));
		assertFalse(type.parseFlag("F"));
		assertFalse(type.parseFlag("NO"));
		assertFalse(type.parseFlag("N"));
		assertFalse(type.parseFlag("OFF"));

		//When used as a flaggable type, null is considered true.
		assertTrue(type.parseFlag(null));
	}

	@Test
	public void testParseAsStandardType() throws ParsingException {

		FlagType type = FlagType.instance();

		// True values (these are canonically true according to AndHow)
		assertTrue(type.parse("true"));
		assertTrue(type.parse("t"));
		assertTrue(type.parse("yes"));
		assertTrue(type.parse("y"));
		assertTrue(type.parse("on"));

		// True values (uppercase is OK)
		assertTrue(type.parse("TRUE"));
		assertTrue(type.parse("TrUe"));
		assertTrue(type.parse("T"));
		assertTrue(type.parse("YES"));
		assertTrue(type.parse("Y"));
		assertTrue(type.parse("ON"));

		// False values (canonically false according to AndHow)
		assertFalse(type.parse("false"));
		assertFalse(type.parse("f"));
		assertFalse(type.parse("no"));
		assertFalse(type.parse("n"));
		assertFalse(type.parse("off"));

		// False values (UPPERCASE is OK)
		assertFalse(type.parse("FALSE"));
		assertFalse(type.parse("FaLsE"));
		assertFalse(type.parse("F"));
		assertFalse(type.parse("NO"));
		assertFalse(type.parse("N"));
		assertFalse(type.parse("OFF"));

		//This looks wrong, but it is not:  It is the behavior of non-FlaggableType.
		// To get the 'flaggable' behavior, the caller must call parseFlag instead of parse.
		assertNull(type.parse(null));
	}
}