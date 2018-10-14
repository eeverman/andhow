package org.yarnandtail.andhow.valid;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ericeverman
 */
public class DblValidatorTest {

	private static String EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE = "THIS VALIDATION IS ALWAYS VALID";

	@Test
	public void testGreaterThanIsSpecificationValid() {
		DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertTrue(instance.isSpecificationValid());
		
		instance = new DblValidator.GreaterThan(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testGreaterThanIsValid() {
		DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertFalse(instance.isValid(4d));
		assertFalse(instance.isValid(5d));
		assertTrue(instance.isValid(6d));
		assertFalse(instance.isValid(null));
		
		instance = new DblValidator.GreaterThan(0d);
		assertFalse(instance.isValid(-1d));
		assertFalse(instance.isValid(0d));
		assertTrue(instance.isValid(1d));
		
		instance = new DblValidator.GreaterThan(-99d);
		assertFalse(instance.isValid(-100d));
		assertFalse(instance.isValid(-99d));
		assertTrue(instance.isValid(-98d));
	}
	
	@Test
	public void testGreaterThanOrEqualToIsSpecificationValid() {
		DblValidator.GreaterThanOrEqualTo instance = new DblValidator.GreaterThanOrEqualTo(5d);
		assertTrue(instance.isSpecificationValid());
		
		instance = new DblValidator.GreaterThanOrEqualTo(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testGreaterThanOrEqualToIsValid() {
		DblValidator.GreaterThanOrEqualTo instance = new DblValidator.GreaterThanOrEqualTo(5d);
		assertFalse(instance.isValid(4d));
		assertTrue(instance.isValid(5d));
		assertTrue(instance.isValid(6d));
		assertFalse(instance.isValid(null));
		
		instance = new DblValidator.GreaterThanOrEqualTo(0d);
		assertFalse(instance.isValid(-1d));
		assertTrue(instance.isValid(0d));
		assertTrue(instance.isValid(1d));
		
		
		instance = new DblValidator.GreaterThanOrEqualTo(-99d);
		assertFalse(instance.isValid(-100d));
		assertTrue(instance.isValid(-99d));
		assertTrue(instance.isValid(-98d));
	}
	

	@Test
	public void testLessThanIsSpecificationValid() {
		DblValidator.LessThan instance = new DblValidator.LessThan(5d);
		assertTrue(instance.isSpecificationValid());
		
		instance = new DblValidator.LessThan(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testLessThanIsValid() {
		DblValidator.LessThan instance = new DblValidator.LessThan(5d);
		assertTrue(instance.isValid(4d));
		assertFalse(instance.isValid(5d));
		assertFalse(instance.isValid(6d));
		assertFalse(instance.isValid(null));
		
		instance = new DblValidator.LessThan(0d);
		assertTrue(instance.isValid(-1d));
		assertFalse(instance.isValid(0d));
		assertFalse(instance.isValid(1d));
		
		instance = new DblValidator.LessThan(-99d);
		assertTrue(instance.isValid(-100d));
		assertFalse(instance.isValid(-99d));
		assertFalse(instance.isValid(-98d));
	}
	
	@Test
	public void testLessThanOrEqualsToIsSpecificationValid() {
		DblValidator.LessThanOrEqualTo instance = new DblValidator.LessThanOrEqualTo(5d);
		assertTrue(instance.isSpecificationValid());
		
		instance = new DblValidator.LessThanOrEqualTo(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}
	
	@Test
	public void testLessThanOrEqualToIsValid() {
		DblValidator.LessThanOrEqualTo instance = new DblValidator.LessThanOrEqualTo(5d);
		assertTrue(instance.isValid(4d));
		assertTrue(instance.isValid(5d));
		assertFalse(instance.isValid(6d));
		
		instance = new DblValidator.LessThanOrEqualTo(0d);
		assertTrue(instance.isValid(-1d));
		assertTrue(instance.isValid(0d));
		assertFalse(instance.isValid(1d));
		
		instance = new DblValidator.LessThanOrEqualTo(-99d);
		assertTrue(instance.isValid(-100d));
		assertTrue(instance.isValid(-99d));
		assertFalse(instance.isValid(-98d));
		
	}
	
	@Test
	public void testGreaterThanInvalidSpecificationMessage() {
		DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void testGreaterThanOrEqualToInvalidSpecificationMessage() {
		DblValidator.GreaterThanOrEqualTo instance = new DblValidator.GreaterThanOrEqualTo(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void testLessThanInvalidSpecificationMessage() {
		DblValidator.LessThan instance = new DblValidator.LessThan(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void testLessThanOrEqualToInvalidSpecificationMessage() {
		DblValidator.LessThanOrEqualTo instance = new DblValidator.LessThanOrEqualTo(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	
}
