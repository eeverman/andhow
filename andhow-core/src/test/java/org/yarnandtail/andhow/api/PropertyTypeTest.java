package org.yarnandtail.andhow.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PropertyTypeTest {

	@Test
	public void testIsFlag() {
		assertTrue(PropertyType.FLAG.isFlag());
		assertFalse(PropertyType.SINGLE_NAME_VALUE.isFlag());
	}
	
	@Test
	public void testIsReal() {
		assertTrue(PropertyType.FLAG.isReal());
		assertTrue(PropertyType.SINGLE_NAME_VALUE.isReal());		
	}
	
	@Test
	public void testIsMultipleOk() {
		assertFalse(PropertyType.FLAG.isMultipleOk());
		assertFalse(PropertyType.SINGLE_NAME_VALUE.isMultipleOk());
	}
	
	@Test
	public void testIsAccumulate() {
		assertFalse(PropertyType.FLAG.isAccumulate());
		assertFalse(PropertyType.SINGLE_NAME_VALUE.isAccumulate());
	}
	
	@Test
	public void testIsNotReal() {
		assertFalse(PropertyType.FLAG.isNotReal());
		assertFalse(PropertyType.SINGLE_NAME_VALUE.isNotReal());
	}
			
	
}
