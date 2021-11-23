package org.yarnandtail.andhow.valid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntValidatorTest {

	@Test
	public void greaterThanIsSpecificationValid() {
		IntValidator.GreaterThan instance = new IntValidator.GreaterThan(5);
		assertTrue(instance.isSpecificationValid());

		instance = new IntValidator.GreaterThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void greaterThanIsValid() {
		IntValidator.GreaterThan instance = new IntValidator.GreaterThan(5);
		assertFalse(instance.isValid(4));
		assertFalse(instance.isValid(5));
		assertTrue(instance.isValid(6));

		instance = new IntValidator.GreaterThan(0);
		assertFalse(instance.isValid(-1));
		assertFalse(instance.isValid(0));
		assertTrue(instance.isValid(1));

		instance = new IntValidator.GreaterThan(-99);
		assertFalse(instance.isValid(-100));
		assertFalse(instance.isValid(-99));
		assertTrue(instance.isValid(-98));
	}

	@Test
	public void greaterThanIsValidThrowsExceptionForNull() {
		final IntValidator.GreaterThan instance = new IntValidator.GreaterThan(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void greaterThanOrEqualToIsSpecificationValid() {
		IntValidator.GreaterThanOrEqualTo instance = new IntValidator.GreaterThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());

		instance = new IntValidator.GreaterThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void greaterThanOrEqualToIsValid() {
		IntValidator.GreaterThanOrEqualTo instance = new IntValidator.GreaterThanOrEqualTo(5);
		assertFalse(instance.isValid(4));
		assertTrue(instance.isValid(5));
		assertTrue(instance.isValid(6));

		instance = new IntValidator.GreaterThanOrEqualTo(0);
		assertFalse(instance.isValid(-1));
		assertTrue(instance.isValid(0));
		assertTrue(instance.isValid(1));

		instance = new IntValidator.GreaterThanOrEqualTo(-99);
		assertFalse(instance.isValid(-100));
		assertTrue(instance.isValid(-99));
		assertTrue(instance.isValid(-98));
	}

	@Test
	public void greaterThanOrEqualToIsValidThrowsExceptionForNull() {
		final IntValidator.GreaterThanOrEqualTo instance = new IntValidator.GreaterThanOrEqualTo(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}


	@Test
	public void lessThanIsSpecificationValid() {
		IntValidator.LessThan instance = new IntValidator.LessThan(5);
		assertTrue(instance.isSpecificationValid());

		instance = new IntValidator.LessThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void lessThanIsValid() {
		IntValidator.LessThan instance = new IntValidator.LessThan(5);
		assertTrue(instance.isValid(4));
		assertFalse(instance.isValid(5));
		assertFalse(instance.isValid(6));

		instance = new IntValidator.LessThan(0);
		assertTrue(instance.isValid(-1));
		assertFalse(instance.isValid(0));
		assertFalse(instance.isValid(1));

		instance = new IntValidator.LessThan(-99);
		assertTrue(instance.isValid(-100));
		assertFalse(instance.isValid(-99));
		assertFalse(instance.isValid(-98));
	}

	@Test
	public void lessThanIsValidThrowsExceptionForNull() {
		final IntValidator.LessThan instance = new IntValidator.LessThan(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void lessThanOrEqualsToIsSpecificationValid() {
		IntValidator.LessThanOrEqualTo instance = new IntValidator.LessThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());

		instance = new IntValidator.LessThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void lessThanOrEqualToIsValid() {
		IntValidator.LessThanOrEqualTo instance = new IntValidator.LessThanOrEqualTo(5);
		assertTrue(instance.isValid(4));
		assertTrue(instance.isValid(5));
		assertFalse(instance.isValid(6));

		instance = new IntValidator.LessThanOrEqualTo(0);
		assertTrue(instance.isValid(-1));
		assertTrue(instance.isValid(0));
		assertFalse(instance.isValid(1));

		instance = new IntValidator.LessThanOrEqualTo(-99);
		assertTrue(instance.isValid(-100));
		assertTrue(instance.isValid(-99));
		assertFalse(instance.isValid(-98));
	}

	@Test
	public void lessThanOrEqualToIsValidThrowsExceptionForNull() {
		final IntValidator.LessThanOrEqualTo instance = new IntValidator.LessThanOrEqualTo(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void invalidMessages() {
		IntValidator.GreaterThan greaterThan = new IntValidator.GreaterThan(4);
		assertEquals("be greater than 4", greaterThan.getTheValueMustDescription());
		assertEquals("The value '3' must be greater than 4", greaterThan.getInvalidMessage(3));

		IntValidator.GreaterThanOrEqualTo greaterThanOrEqualTo = new IntValidator.GreaterThanOrEqualTo(4);
		assertEquals("be greater than or equal to 4", greaterThanOrEqualTo.getTheValueMustDescription());
		assertEquals("The value '3' must be greater than or equal to 4", greaterThanOrEqualTo.getInvalidMessage(3));

		IntValidator.LessThan lessThan = new IntValidator.LessThan(4);
		assertEquals("be less than 4", lessThan.getTheValueMustDescription());
		assertEquals("The value '5' must be less than 4", lessThan.getInvalidMessage(5));


		IntValidator.LessThanOrEqualTo lessThanOrEqualTo = new IntValidator.LessThanOrEqualTo(4);
		assertEquals("be less than or equal to 4", lessThanOrEqualTo.getTheValueMustDescription());
		assertEquals("The value '5' must be less than or equal to 4", lessThanOrEqualTo.getInvalidMessage(5));
	}

	@Test
	public void invalidSpecificationMessage() {
		IntValidator.GreaterThan greaterThan = new IntValidator.GreaterThan(4);
		assertEquals("THIS VALIDATION IS ALWAYS VALID", greaterThan.getInvalidSpecificationMessage());
	}
}
