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

		assertSame(ft1, ft2);
		assertNotSame(ft1, ft3);
	}

	@Test
	void parseFlagIsTrueIfNull() throws ParsingException {
		FlagType ft1 = FlagType.instance();

		assertTrue(ft1.parseFlag(null));
	}


	// This test just copies some testing from BolTypeTest, but not all of it.
	@Test
	public void parseFlagDelegatesToBolParseIfNotNull() throws ParsingException {

		BolType type = BolType.instance();

		// True values (these are canonically true according to AndHow)
		assertTrue(type.parse("true"));
		assertTrue(type.parse("t"));
		assertTrue(type.parse("yes"));
		assertTrue(type.parse("y"));
		assertTrue(type.parse("on"));

		// False values
		assertFalse(type.parse(""));
		assertFalse(type.parse(" anything "));
		assertFalse(type.parse("NoWhitespaceIsAlsoFalseIfNotRecognized"));

	}
}