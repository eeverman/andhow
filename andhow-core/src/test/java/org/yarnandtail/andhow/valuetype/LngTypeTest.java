package org.yarnandtail.andhow.valuetype;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

/**
 *
 * @author ericeverman
 */
public class LngTypeTest {

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
		assertFalse(type.isParsable("1234.1234"));

		assertThrows(ParsingException.class, () ->
			type.parse("1234.1234")
		);
	}
	
	@Test
	public void testParseNotANumber() throws ParsingException {
		LngType type = LngType.instance();
		assertFalse(type.isParsable("apple"));

		assertThrows(ParsingException.class, () ->
			type.parse("apple")
		);
	}
	
	@Test
	public void testParseEmpty() throws ParsingException {
		LngType type = LngType.instance();
		assertFalse(type.isParsable(""));

		assertThrows(ParsingException.class, () ->
			type.parse("")
		);
	}
	
	@Test
	public void testParseTooBig() {
		LngType type = LngType.instance();
		assertFalse(type.isParsable("9999999999999999999999999999999999999999"));

		assertThrows(ParsingException.class, () ->
			type.parse("9999999999999999999999999999999999999999")
		);
	}
	
	@Test
	public void testParseTooSmall() {
		LngType type = LngType.instance();
		assertFalse(type.isParsable("-9999999999999999999999999999999999999999"));

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
