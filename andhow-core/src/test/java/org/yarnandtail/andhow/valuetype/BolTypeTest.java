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
	public void testParseRecognizedValues() throws ParsingException {

		BolType type = BolType.instance();

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

		// Null is expected and just returned as null
		assertNull(type.parse(null));
	}


	@Test
	public void testParseUnrecognizedValues() throws ParsingException {

		BolType type = BolType.instance();

		// Unrecognized values
		assertThrows(ParsingException.class, () -> type.parse("XYZ"));

		//Extra spaces are not OK
		assertThrows(ParsingException.class, () -> type.parse(" true "));
		assertThrows(ParsingException.class, () -> type.parse(" false "));

	}

	@Test
	public void testCast() {

		BolType type = BolType.instance();

		Object o = Boolean.valueOf(true);
		assertEquals(Boolean.TRUE, type.cast(o));
		assertTrue(type.cast(o) instanceof Boolean);
	}

}
