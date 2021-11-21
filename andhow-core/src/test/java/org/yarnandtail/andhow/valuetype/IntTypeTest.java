/*
 */
package org.yarnandtail.andhow.valuetype;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import static org.junit.jupiter.api.Assertions.*;

public class IntTypeTest {

	@Test
	public void testInstance() {
		IntType t1 = IntType.instance();
		IntType t2 = IntType.instance();
		assertNotNull(t1);
		assertSame(t1, t2);
	}

	@Test
	public void testParseHappyPath() throws ParsingException {

		IntType type = IntType.instance();

		assertEquals(new Integer(-1234), type.parse("-1234"));
		assertEquals(new Integer(0), type.parse("0"));
		assertNull(type.parse(null));
	}

	@Test
	public void testParseDecimalNumber() {
		IntType type = IntType.instance();

		assertThrows(ParsingException.class, () ->
																						 type.parse("1234.1234")
		);
	}

	@Test
	public void testParseNotANumber() {
		IntType type = IntType.instance();

		assertThrows(ParsingException.class, () ->
																						 type.parse("apple")
		);
	}

	@Test
	public void testParseEmpty() throws ParsingException {
		IntType type = IntType.instance();

		assertThrows(ParsingException.class, () ->
																						 type.parse("")
		);
	}

	@Test
	public void testParseTooBig() {
		IntType type = IntType.instance();

		assertThrows(ParsingException.class, () ->
																						 type.parse("9999999999999999999999999999999999999999")
		);
	}

	@Test
	public void testParseTooSmall() {
		IntType type = IntType.instance();

		assertThrows(ParsingException.class, () ->
																						 type.parse("-9999999999999999999999999999999999999999")
		);
	}

	@Test
	public void testCast() {

		IntType type = IntType.instance();

		Object o = new Integer(999);
		assertEquals(new Integer(999), type.cast(o));
		assertTrue(type.cast(o) instanceof Integer);
	}

}
