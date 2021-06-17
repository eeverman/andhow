package org.yarnandtail.andhow;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.property.StrProp;

import static org.junit.jupiter.api.Assertions.*;
import static org.yarnandtail.andhow.SimpleParams.*;

/**
 *
 * @author ericeverman
 */
public class PropertyValueTest {
	
	private static final PropertyValue SPV1 = new PropertyValue(STR_BOB, "abc");
	private static final PropertyValue SPV2 = new PropertyValue(STR_BOB, "abc");
	private static final PropertyValue SPV3 = new PropertyValue(STR_BOB, "def");
	private static final PropertyValue SPV4 = new PropertyValue(STR_BOB, null);
	private static final PropertyValue SPV5 = new PropertyValue(STR_NULL, "abc");
	private static final PropertyValue SPV6 = new PropertyValue(STR_NULL, "abc");
	private static final PropertyValue SPV7 = new PropertyValue(STR_NULL, "def");
	private static final PropertyValue SPV8 = new PropertyValue(STR_NULL, null);
	
	/**
	 * Test of equals method, of class PropertyValue.
	 */
	@Test
	public void testEquals() {
		
		//Try some string properties


		//
		assertTrue(SPV1.equals(SPV1));
		assertTrue(SPV1.equals(SPV2));
		assertFalse(SPV1.equals(SPV3));
		assertFalse(SPV1.equals(SPV4));
		assertFalse(SPV1.equals(SPV5));
		assertFalse(SPV1.equals(SPV6));
		assertFalse(SPV1.equals(SPV7));
		assertFalse(SPV1.equals(SPV8));
		//
		assertTrue(SPV2.equals(SPV1));
		assertTrue(SPV2.equals(SPV2));
		assertFalse(SPV2.equals(SPV3));
		assertFalse(SPV2.equals(SPV4));
		assertFalse(SPV2.equals(SPV5));
		assertFalse(SPV2.equals(SPV6));
		assertFalse(SPV2.equals(SPV7));
		assertFalse(SPV2.equals(SPV8));
		//
		assertFalse(SPV3.equals(SPV1));
		assertFalse(SPV3.equals(SPV2));
		assertTrue(SPV3.equals(SPV3));
		assertFalse(SPV3.equals(SPV4));
		assertFalse(SPV3.equals(SPV5));
		assertFalse(SPV3.equals(SPV6));
		assertFalse(SPV3.equals(SPV7));
		assertFalse(SPV3.equals(SPV8));
		//
		assertFalse(SPV4.equals(SPV1));
		assertFalse(SPV4.equals(SPV2));
		assertFalse(SPV4.equals(SPV3));
		assertTrue(SPV4.equals(SPV4));
		assertFalse(SPV4.equals(SPV5));
		assertFalse(SPV4.equals(SPV6));
		assertFalse(SPV4.equals(SPV7));
		assertFalse(SPV4.equals(SPV8));
		//
		assertFalse(SPV5.equals(SPV1));
		assertFalse(SPV5.equals(SPV2));
		assertFalse(SPV5.equals(SPV3));
		assertFalse(SPV5.equals(SPV4));
		assertTrue(SPV5.equals(SPV5));
		assertTrue(SPV5.equals(SPV6));
		assertFalse(SPV5.equals(SPV7));
		assertFalse(SPV5.equals(SPV8));
		//
		assertFalse(SPV1.equals(null));
	}

	/**
	 * Test of hashCode method, of class PropertyValue.
	 */
	@Test
	public void testHashCode() {
		assertTrue(SPV1.hashCode() != 0);
		assertEquals(SPV1.hashCode(), SPV2.hashCode());
		assertNotEquals(SPV1.hashCode(), SPV3.hashCode());
		assertNotEquals(SPV1.hashCode(), SPV4.hashCode());
		assertNotEquals(SPV1.hashCode(), SPV5.hashCode());
		assertNotEquals(SPV1.hashCode(), SPV6.hashCode());
		assertNotEquals(SPV1.hashCode(), SPV7.hashCode());
		assertNotEquals(SPV1.hashCode(), SPV8.hashCode());
		//
		assertTrue(SPV2.hashCode() != 0);
		assertEquals(SPV2.hashCode(), SPV1.hashCode());
		assertNotEquals(SPV2.hashCode(), SPV3.hashCode());
		assertNotEquals(SPV2.hashCode(), SPV4.hashCode());
		assertNotEquals(SPV2.hashCode(), SPV5.hashCode());
		assertNotEquals(SPV2.hashCode(), SPV6.hashCode());
		assertNotEquals(SPV2.hashCode(), SPV7.hashCode());
		assertNotEquals(SPV2.hashCode(), SPV8.hashCode());
		//
		assertTrue(SPV3.hashCode() != 0);
		assertNotEquals(SPV3.hashCode(), SPV1.hashCode());
		assertNotEquals(SPV3.hashCode(), SPV2.hashCode());
		assertNotEquals(SPV3.hashCode(), SPV4.hashCode());
		assertNotEquals(SPV3.hashCode(), SPV5.hashCode());
		assertNotEquals(SPV3.hashCode(), SPV6.hashCode());
		assertNotEquals(SPV3.hashCode(), SPV7.hashCode());
		assertNotEquals(SPV3.hashCode(), SPV8.hashCode());
		//
		assertTrue(SPV4.hashCode() != 0);
		assertNotEquals(SPV4.hashCode(), SPV1.hashCode());
		assertNotEquals(SPV4.hashCode(), SPV2.hashCode());
		assertNotEquals(SPV4.hashCode(), SPV3.hashCode());
		assertNotEquals(SPV4.hashCode(), SPV5.hashCode());
		assertNotEquals(SPV4.hashCode(), SPV6.hashCode());
		assertNotEquals(SPV4.hashCode(), SPV7.hashCode());
		assertNotEquals(SPV4.hashCode(), SPV8.hashCode());
	}

	/**
	 * Test of getProperty method, of class PropertyValue.
	 */
	@Test
	public void testGetProperty() {
		assertEquals(SPV1.getProperty(), STR_BOB);
		assertEquals(SPV5.getProperty(), STR_NULL);
	}

	/**
	 * Test of getValue method, of class PropertyValue.
	 */
	@Test
	public void testGetValue() {
		assertEquals(SPV1.getValue(), "abc");
		assertNull(SPV4.getValue());
		assertEquals(SPV5.getValue(), "abc");
	}
	
	@Test
	public void testConstructor() {
		assertThrows(RuntimeException.class, () ->
				new PropertyValue((StrProp)null, null)
		);
	}
	
}
