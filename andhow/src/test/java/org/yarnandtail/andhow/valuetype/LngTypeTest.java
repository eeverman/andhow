/*
 */
package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.valuetype.LngType;
import org.junit.Test;

import static org.junit.Assert.*;

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
	
	@Test(expected=ParsingException.class)
	public void testParseDecimalNumber() throws ParsingException {
		LngType type = LngType.instance();
		assertFalse(type.isParsable("1234.1234"));
		type.parse("1234.1234");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseNotANumber() throws ParsingException {
		LngType type = LngType.instance();
		assertFalse(type.isParsable("apple"));
		type.parse("apple");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseEmpty() throws ParsingException {
		LngType type = LngType.instance();
		assertFalse(type.isParsable(""));
		type.parse("");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseTooBig() throws ParsingException {
		LngType type = LngType.instance();
		assertFalse(type.isParsable("9999999999999999999999999999999999999999"));
		type.parse("9999999999999999999999999999999999999999");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseTooSmall() throws ParsingException {
		LngType type = LngType.instance();
		assertFalse(type.isParsable("-9999999999999999999999999999999999999999"));
		type.parse("-9999999999999999999999999999999999999999");
	}
	
	@Test
	public void testCast() {
		
		LngType type = LngType.instance();
		
		Object o = new Long(999);
		assertEquals(new Long(999L), type.cast(o));
		assertTrue(type.cast(o) instanceof Long);
	}
	
}
