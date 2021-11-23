package org.yarnandtail.andhow.valid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DblValidatorTest {

	private static String EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE = "THIS VALIDATION IS ALWAYS VALID";

	@Test
	public void greaterThanIsSpecificationValid() {
		DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertTrue(instance.isSpecificationValid());

		instance = new DblValidator.GreaterThan(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void greaterThanIsValid() {
		DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertFalse(instance.isValid(4d));
		assertFalse(instance.isValid(5d));
		assertTrue(instance.isValid(6d));

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
	public void greaterThanIsValidThrowsExceptionForNull() {
		final DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void greaterThanGetTheValueMustDescription() {
		final DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertEquals("be greater than 5.0", instance.getTheValueMustDescription());
	}

	@Test
	public void greaterThanGetInvalidMessage() {
		final DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertEquals("The value '3.0' must be greater than 5.0", instance.getInvalidMessage(3D));
	}

	@Test
	public void greaterThanOrEqualToIsSpecificationValid() {
		DblValidator.GreaterThanOrEqualTo instance = new DblValidator.GreaterThanOrEqualTo(5d);
		assertTrue(instance.isSpecificationValid());

		instance = new DblValidator.GreaterThanOrEqualTo(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void greaterThanOrEqualToIsValid() {
		DblValidator.GreaterThanOrEqualTo instance = new DblValidator.GreaterThanOrEqualTo(5d);
		assertFalse(instance.isValid(4d));
		assertTrue(instance.isValid(5d));
		assertTrue(instance.isValid(6d));

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
	public void greaterThanOrEqualToIsValidThrowsExceptionForNull() {
		final DblValidator.GreaterThanOrEqualTo instance = new DblValidator.GreaterThanOrEqualTo(5d);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}


	@Test
	public void lessThanIsSpecificationValid() {
		DblValidator.LessThan instance = new DblValidator.LessThan(5d);
		assertTrue(instance.isSpecificationValid());

		instance = new DblValidator.LessThan(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void lessThanIsValid() {
		DblValidator.LessThan instance = new DblValidator.LessThan(5d);
		assertTrue(instance.isValid(4d));
		assertFalse(instance.isValid(5d));
		assertFalse(instance.isValid(6d));

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
	public void lessThanIsValidThrowsExceptionForNull() {
		final DblValidator.LessThan instance = new DblValidator.LessThan(5d);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void lessThanOrEqualsToIsSpecificationValid() {
		DblValidator.LessThanOrEqualTo instance = new DblValidator.LessThanOrEqualTo(5d);
		assertTrue(instance.isSpecificationValid());

		instance = new DblValidator.LessThanOrEqualTo(-999999999d);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void lessThanOrEqualToIsValid() {
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
	public void lessThanOrEqualToIsValidThrowsExceptionForNull() {
		final DblValidator.LessThanOrEqualTo instance = new DblValidator.LessThanOrEqualTo(5d);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void greaterThanInvalidSpecificationMessage() {
		DblValidator.GreaterThan instance = new DblValidator.GreaterThan(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void greaterThanOrEqualToInvalidSpecificationMessage() {
		DblValidator.GreaterThanOrEqualTo instance = new DblValidator.GreaterThanOrEqualTo(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void lessThanInvalidSpecificationMessage() {
		DblValidator.LessThan instance = new DblValidator.LessThan(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void lessThanOrEqualToInvalidSpecificationMessage() {
		DblValidator.LessThanOrEqualTo instance = new DblValidator.LessThanOrEqualTo(5d);
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

}
