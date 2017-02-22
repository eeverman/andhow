package org.yarnandtail.andhow.valid;

import org.yarnandtail.andhow.valid.LngValidator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class LngValidatorTest {

	@Test
	public void testGreaterThanIsSpecificationValid() {
		LngValidator.GreaterThan instance = new LngValidator.GreaterThan(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new LngValidator.GreaterThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testGreaterThanIsValid() {
		LngValidator.GreaterThan instance = new LngValidator.GreaterThan(5);
		assertFalse(instance.isValid(4L));
		assertFalse(instance.isValid(5L));
		assertTrue(instance.isValid(6L));
		
		instance = new LngValidator.GreaterThan(0);
		assertFalse(instance.isValid(-1L));
		assertFalse(instance.isValid(0L));
		assertTrue(instance.isValid(1L));
		
		instance = new LngValidator.GreaterThan(-99);
		assertFalse(instance.isValid(-100L));
		assertFalse(instance.isValid(-99L));
		assertTrue(instance.isValid(-98L));
	}
	
	@Test
	public void testGreaterThanOrEqualToIsSpecificationValid() {
		LngValidator.GreaterThanOrEqualTo instance = new LngValidator.GreaterThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new LngValidator.GreaterThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testGreaterThanOrEqualToIsValid() {
		LngValidator.GreaterThanOrEqualTo instance = new LngValidator.GreaterThanOrEqualTo(5);
		assertFalse(instance.isValid(4L));
		assertTrue(instance.isValid(5L));
		assertTrue(instance.isValid(6L));
		
		instance = new LngValidator.GreaterThanOrEqualTo(0);
		assertFalse(instance.isValid(-1L));
		assertTrue(instance.isValid(0L));
		assertTrue(instance.isValid(1L));
		
		
		instance = new LngValidator.GreaterThanOrEqualTo(-99);
		assertFalse(instance.isValid(-100L));
		assertTrue(instance.isValid(-99L));
		assertTrue(instance.isValid(-98L));
	}
	

	@Test
	public void testLessThanIsSpecificationValid() {
		LngValidator.LessThan instance = new LngValidator.LessThan(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new LngValidator.LessThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testLessThanIsValid() {
		LngValidator.LessThan instance = new LngValidator.LessThan(5);
		assertTrue(instance.isValid(4L));
		assertFalse(instance.isValid(5L));
		assertFalse(instance.isValid(6L));
		
		instance = new LngValidator.LessThan(0);
		assertTrue(instance.isValid(-1L));
		assertFalse(instance.isValid(0L));
		assertFalse(instance.isValid(1L));
		
		instance = new LngValidator.LessThan(-99);
		assertTrue(instance.isValid(-100L));
		assertFalse(instance.isValid(-99L));
		assertFalse(instance.isValid(-98L));
	}
	
	@Test
	public void testLessThanOrEqualsToIsSpecificationValid() {
		LngValidator.LessThanOrEqualTo instance = new LngValidator.LessThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new LngValidator.LessThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}
	
	@Test
	public void testLessThanOrEqualToIsValid() {
		LngValidator.LessThanOrEqualTo instance = new LngValidator.LessThanOrEqualTo(5);
		assertTrue(instance.isValid(4L));
		assertTrue(instance.isValid(5L));
		assertFalse(instance.isValid(6L));
		
		instance = new LngValidator.LessThanOrEqualTo(0);
		assertTrue(instance.isValid(-1L));
		assertTrue(instance.isValid(0L));
		assertFalse(instance.isValid(1L));
		
		instance = new LngValidator.LessThanOrEqualTo(-99);
		assertTrue(instance.isValid(-100L));
		assertTrue(instance.isValid(-99L));
		assertFalse(instance.isValid(-98L));
		
	}
	

	
}
