package org.yarnandtail.andhow.valuetype;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import static org.junit.jupiter.api.Assertions.*;

public class LngTypeTest {

	@Test
	public void testInstance() {
		LngType t1 = LngType.instance();
		LngType t2 = LngType.instance();
		assertNotNull(t1);
		assertSame(t1, t2);
	}

	@Test
	public void testParseHappyPath() throws ParsingException {
		
		LngType type = LngType.instance();
		
		assertEquals(new Long(-1234), type.parse("-1234"));
		assertEquals(new Long(0), type.parse("0"));
		assertNull(type.parse(null));
	}
	
	@Test
	public void testParseDecimalNumber() {
		LngType type = LngType.instance();

		assertThrows(ParsingException.class, () ->
			type.parse("1234.1234")
		);
	}
	
	@Test
	public void testParseNotANumber() throws ParsingException {
		LngType type = LngType.instance();

		assertThrows(ParsingException.class, () ->
			type.parse("apple")
		);
	}
	
	@Test
	public void testParseEmpty() throws ParsingException {
		LngType type = LngType.instance();

		assertThrows(ParsingException.class, () ->
			type.parse("")
		);
	}

	@Test
	public void stringMarkedWithLSufixIsError() {
		LngType type = LngType.instance();
		assertThrows(ParsingException.class, () ->
				type.parse("34L")
		);
	}
	
	@Test
	public void testParseTooBig() {
		LngType type = LngType.instance();

		assertThrows(ParsingException.class, () ->
			type.parse("9999999999999999999999999999999999999999")
		);
	}
	
	@Test
	public void testParseTooSmall() {
		LngType type = LngType.instance();

		assertThrows(ParsingException.class, () ->
			type.parse("-9999999999999999999999999999999999999999")
		);
	}
	
	@Test
	public void testCast() {
		
		LngType type = LngType.instance();
		
		Object o = new Long(999);
		assertEquals(new Long(999L), type.cast(o));
		assertTrue(type.cast(o) instanceof Long);
	}
	
}
