/*
 */
package org.yarnandtail.andhow.valuetype;

import static org.junit.Assert.*;
import org.junit.Test;
import org.yarnandtail.andhow.api.ParsingException;

/**
 *
 * @author ericeverman
 */
public class IntTypeTest {
	


	@Test
	public void testParseHappyPath() throws ParsingException {
		
		IntType type = IntType.instance();
		
		assertEquals(new Integer(-1234), type.parse("-1234"));
		assertEquals(new Integer(0), type.parse("0"));
		assertNull(type.parse(null));
	}
	
	@Test(expected=ParsingException.class)
	public void testParseDecimalNumber() throws ParsingException {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("1234.1234"));
		type.parse("1234.1234");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseNotANumber() throws ParsingException {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("apple"));
		type.parse("apple");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseEmpty() throws ParsingException {
		IntType type = IntType.instance();
		assertFalse(type.isParsable(""));
		type.parse("");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseTooBig() throws ParsingException {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("9999999999999999999999999999999999999999"));
		type.parse("9999999999999999999999999999999999999999");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseTooSmall() throws ParsingException {
		IntType type = IntType.instance();
		assertFalse(type.isParsable("-9999999999999999999999999999999999999999"));
		type.parse("-9999999999999999999999999999999999999999");
	}
	
	@Test
	public void testCast() {
		
		IntType type = IntType.instance();
		
		Object o = new Integer(999);
		assertEquals(new Integer(999), type.cast(o));
		assertTrue(type.cast(o) instanceof Integer);
	}
	
}
