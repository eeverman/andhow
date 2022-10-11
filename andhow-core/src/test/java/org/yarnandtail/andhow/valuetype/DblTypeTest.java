package org.yarnandtail.andhow.valuetype;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import static org.junit.jupiter.api.Assertions.*;


public class DblTypeTest {

	private static final double COMP_ERR = .0000001;

	@Test
	public void testInstance() {
		DblType t1 = DblType.instance();
		DblType t2 = DblType.instance();
		assertNotNull(t1);
		assertSame(t1, t2);
	}

	@Test
	public void testParseHappyPath() throws ParsingException {

		DblType type = DblType.instance();

		assertEquals(Double.valueOf(34d), type.parse("34D"), COMP_ERR);
		assertEquals(Double.valueOf(34d), type.parse("34f"), COMP_ERR);  //fFdD all ok
		assertEquals(Double.valueOf(-1234.5678d), type.parse("-1234.5678d"), COMP_ERR);
		assertEquals(Double.valueOf(0), type.parse("0"), COMP_ERR);
		assertNull(type.parse(null));
	}

	@Test
	public void testWeirdDoubleSpecialValues() throws ParsingException {
		DblType type = DblType.instance();

		assertEquals(Double.NaN, type.parse("NaN"), COMP_ERR);
		assertEquals(Double.NaN, type.parse("+NaN"), COMP_ERR);
		assertEquals(Double.NaN, type.parse("-NaN"), COMP_ERR);
		assertEquals(Double.POSITIVE_INFINITY, type.parse("Infinity"), COMP_ERR);
		assertEquals(Double.POSITIVE_INFINITY, type.parse("+Infinity"), COMP_ERR);
		assertEquals(Double.NEGATIVE_INFINITY, type.parse("-Infinity"), COMP_ERR);

	}

	@Test
	public void testParseNotANumber() {
		DblType type = DblType.instance();

		assertThrows(ParsingException.class, () ->
																						 type.parse("apple")
		);
	}

	@Test
	public void stringMarkedAsLongIsError() {
		DblType type = DblType.instance();
		assertThrows(ParsingException.class, () ->
																						 type.parse("34L")
		);
	}

	@Test
	public void testParseEmpty() {
		DblType type = DblType.instance();

		assertThrows(ParsingException.class, () ->
																						 type.parse("")
		);
	}

	@Test
	public void testCast() {

		DblType type = DblType.instance();

		Object o = Double.valueOf(999);
		assertEquals(Double.valueOf(999), type.cast(o), COMP_ERR);
		assertTrue(type.cast(o) instanceof Double);
	}

}
