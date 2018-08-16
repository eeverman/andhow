package org.yarnandtail.andhow;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.yarnandtail.andhow.SimpleParams.*;

/**
 *
 * @author ericeverman
 */
public class PropertyValueTest {
	
	public PropertyValueTest() {
	}

	/**
	 * Test of equals method, of class PropertyValue.
	 */
	@Test
	public void testEquals() {
		
		//Try some string properties
		PropertyValue spv1 = new PropertyValue(STR_BOB, "abc");
		PropertyValue spv2 = new PropertyValue(STR_BOB, "abc");
		PropertyValue spv3 = new PropertyValue(STR_BOB, "def");
		PropertyValue spv4 = new PropertyValue(STR_BOB, null);
		PropertyValue spv5 = new PropertyValue(STR_NULL, "abc");
		PropertyValue spv6 = new PropertyValue(STR_NULL, "abc");
		PropertyValue spv7 = new PropertyValue(STR_NULL, "def");
		PropertyValue spv8 = new PropertyValue(STR_NULL, null);

		//
		assertTrue(spv1.equals(spv1));
		assertTrue(spv1.equals(spv2));
		assertFalse(spv1.equals(spv3));
		assertFalse(spv1.equals(spv4));
		assertFalse(spv1.equals(spv5));
		assertFalse(spv1.equals(spv6));
		assertFalse(spv1.equals(spv7));
		assertFalse(spv1.equals(spv8));
		//
		assertTrue(spv2.equals(spv1));
		assertTrue(spv2.equals(spv2));
		assertFalse(spv2.equals(spv3));
		assertFalse(spv2.equals(spv4));
		assertFalse(spv2.equals(spv5));
		assertFalse(spv2.equals(spv6));
		assertFalse(spv2.equals(spv7));
		assertFalse(spv2.equals(spv8));
		//
		assertFalse(spv3.equals(spv1));
		assertFalse(spv3.equals(spv2));
		assertTrue(spv3.equals(spv3));
		assertFalse(spv3.equals(spv4));
		assertFalse(spv3.equals(spv5));
		assertFalse(spv3.equals(spv6));
		assertFalse(spv3.equals(spv7));
		assertFalse(spv3.equals(spv8));
		//
		assertFalse(spv5.equals(spv1));
		assertFalse(spv5.equals(spv2));
		assertFalse(spv5.equals(spv3));
		assertFalse(spv5.equals(spv4));
		assertTrue(spv5.equals(spv5));
		assertTrue(spv5.equals(spv6));
		assertFalse(spv5.equals(spv7));
		assertFalse(spv5.equals(spv8));
		//
		assertFalse(spv1.equals(null));
	}

	/**
	 * Test of hashCode method, of class PropertyValue.
	 */
	@Test
	public void testHashCode() {
	}

	/**
	 * Test of getProperty method, of class PropertyValue.
	 */
	@Test
	public void testGetProperty() {
	}

	/**
	 * Test of getValue method, of class PropertyValue.
	 */
	@Test
	public void testGetValue() {
	}
	
}
