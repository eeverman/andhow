package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyObjectPairTest {

	@Test
	public void simpleTest() throws ParsingException {
		KeyObjectPair kop1 = new KeyObjectPair("  Name1  ", "  Value1  ");	//two spaces pad start & end of name & value
		KeyObjectPair kop2 = new KeyObjectPair("Name2", "");
		KeyObjectPair kop3 = new KeyObjectPair("Name3", 23L);
		KeyObjectPair kop4 = new KeyObjectPair("  Name4  ");
		KeyObjectPair kop5 = new KeyObjectPair("Name5", null);

		assertEquals("Name1", kop1.getName());
		assertEquals("  Value1  ", kop1.getValue());
		assertEquals("Name1 : \"  Value1  \"", kop1.toString());

		assertEquals("Name2", kop2.getName());
		assertEquals("", kop2.getValue());
		assertEquals("Name2 : \"\"", kop2.toString());

		assertEquals("Name3", kop3.getName());
		assertEquals(23L, kop3.getValue());
		assertEquals("Name3 : \"23\"", kop3.toString());

		assertEquals("Name4", kop4.getName());
		assertNull(kop4.getValue());
		assertEquals("Name4 : \"[null]\"", kop4.toString());

		assertEquals("Name5", kop5.getName());
		assertNull(kop5.getValue());
		assertEquals("Name5 : \"[null]\"", kop5.toString());
	}

	@Test
	public void emptyNameShouldBeAnError() {
		assertThrows(ParsingException.class, () -> {
			new KeyObjectPair("  ", "SomeValue");
		});

		assertThrows(ParsingException.class, () -> {
			new KeyObjectPair("", "SomeValue");
		});

		assertThrows(ParsingException.class, () -> {
			new KeyObjectPair(null, "SomeValue");
		});
	}

}