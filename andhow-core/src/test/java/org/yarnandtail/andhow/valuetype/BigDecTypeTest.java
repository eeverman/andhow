package org.yarnandtail.andhow.valuetype;


import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BigDecTypeTest {

	private static final String PARSE_ERROR_MSG = "Unable to convert to a BigDecimal numeric value";
	private BigDecType type = BigDecType.instance();

	@Test
	public void testInstance() {
		BigDecType t1 = BigDecType.instance();
		BigDecType t2 = BigDecType.instance();
		assertNotNull(t1);
		assertSame(t1, t2);
	}

	@Test
	public void testParse() throws ParsingException {
		assertNull(type.parse(null));
		assertEquals(BigDecimal.ZERO, type.parse("0"));
		assertEquals(BigDecimal.ONE, type.parse("1"));
		assertEquals(new BigDecimal("12.34567"), type.parse("12.34567"));
		assertEquals(new BigDecimal("-300.724578"), type.parse("-300.724578"));
	}

	@Test
	public void testParseEmpty() throws ParsingException {
		assertFalse(type.isParsable(""));

		Exception e = assertThrows(ParsingException.class, () ->
																													 type.parse("")
		);
		assertEquals(PARSE_ERROR_MSG, e.getMessage());
	}

	@Test
	public void testParseNotANumber() throws ParsingException {
		assertFalse(type.isParsable("apple"));

		Exception e = assertThrows(ParsingException.class, () ->
																													 type.parse("apple")
		);
		assertEquals(PARSE_ERROR_MSG, e.getMessage());
	}

	@Test
	public void testCast() {
		Object o = new BigDecimal("123.456");
		assertEquals(new BigDecimal("123.456"), o);
		assertNotNull(type.cast(o));
	}

}
