/*
 */
package org.yarnandtail.andhow.valuetype;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

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
	
	@Test
	public void testParseTooManyDecimalPlaces() {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable("2007-12-03T10:15:30.1234567891"));

		assertThrows(ParsingException.class, () ->
			type.parse("2007-12-03T10:15:30.1234567891")
		);
	}
	
	@Test
	public void testParseEmpty() {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable(""));

		assertThrows(ParsingException.class, () ->
			type.parse("")
		);
	}
	
	@Test
	public void testParseIncorrect24Hour() {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable("2007-12-03T24:15:30.123456789"));

		assertThrows(ParsingException.class, () ->
			type.parse("2007-12-03T24:15:30.123456789")
		);
	}
	
	@Test
	public void testParseMissingZeroPadding() {
		LocalDateTimeType type = LocalDateTimeType.instance();
		assertFalse(type.isParsable("2007-12-03T10:15:3"));

		assertThrows(ParsingException.class, () ->
			type.parse("2007-12-03T10:15:3")
		);
	}
	
	@Test
	public void testCast() {
		
		LocalDateTimeType type = LocalDateTimeType.instance();
		
		Object o = LocalDateTime.parse("2007-12-03T00:00");
		assertEquals(LocalDateTime.parse("2007-12-03T00:00"), type.cast(o));
		assertTrue(type.cast(o) instanceof LocalDateTime);
	}
	
}
