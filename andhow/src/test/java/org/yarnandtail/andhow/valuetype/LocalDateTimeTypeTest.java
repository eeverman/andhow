/*
 */
package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.valuetype.LocalDateTimeType;
import java.time.LocalDateTime;
import org.junit.Test;

import static org.junit.Assert.*;

import org.yarnandtail.andhow.ParsingException;

/**
 *
 * @author ericeverman
 */
public class LocalDateTimeTypeTest {
	


	@Test
	public void testParseHappyPath() throws ParsingException {
		
		LocalDateTimeType type = LocalDateTimeType.instance();

		assertEquals(LocalDateTime.parse("2007-12-03T00:00"), type.parse("2007-12-03T00:00"));
		assertEquals(LocalDateTime.parse("2007-12-03T23:00"), type.parse("2007-12-03T23:00"));
		
		assertEquals(LocalDateTime.parse("0001-12-03T10:15"), type.parse("0001-12-03T10:15"));
		
		assertEquals(LocalDateTime.parse("2007-12-03T10:15"), type.parse("2007-12-03T10:15"));
		assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), type.parse("2007-12-03T10:15:30"));
		assertEquals(LocalDateTime.parse("2007-12-03T10:15:30.123"), type.parse("2007-12-03T10:15:30.123"));
		assertEquals(LocalDateTime.parse("2007-12-03T10:15:30.123456789"), type.parse("2007-12-03T10:15:30.123456789"));
		assertNull(type.parse(null));
	}
	
	@Test(expected=ParsingException.class)
	public void testParseTooManyDecimalPlaces() throws ParsingException {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable("2007-12-03T10:15:30.1234567891"));
		type.parse("2007-12-03T10:15:30.1234567891");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseEmpty() throws ParsingException {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable(""));
		type.parse("");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseIncorrect24Hour() throws ParsingException {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable("2007-12-03T24:15:30.123456789"));
		type.parse("2007-12-03T24:15:30.123456789");
	}
	
	@Test(expected=ParsingException.class)
	public void testParseMissingZeroPadding() throws ParsingException {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable("2007-12-03T10:15:3"));
		type.parse("2007-12-03T10:15:3");
	}
	
	@Test
	public void testCast() {
		
		LocalDateTimeType type = LocalDateTimeType.instance();
		
		Object o = LocalDateTime.parse("2007-12-03T00:00");
		assertEquals(LocalDateTime.parse("2007-12-03T00:00"), type.cast(o));
		assertTrue(type.cast(o) instanceof LocalDateTime);
	}
	
}
