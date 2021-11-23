package org.yarnandtail.andhow.valid;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class LngValidatorTest {

	private static final String ALWAYS_VALID_MESSAGE = "THIS VALIDATION IS ALWAYS VALID";

	@Test
	public void greaterThanIsSpecificationValid() {
		LngValidator.GreaterThan instance = new LngValidator.GreaterThan(5);
		assertTrue(instance.isSpecificationValid());

		instance = new LngValidator.GreaterThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void greaterThanGetInvalidSpecificationMessage() {
		LngValidator.GreaterThan instance = new LngValidator.GreaterThan(5);
		assertEquals(ALWAYS_VALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void greaterThanIsValid() {
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
	public void greaterThanIsValidThrowsExceptionForNull() {
		final LngValidator.GreaterThan instance = new LngValidator.GreaterThan(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void greaterThanGetTheValueMustDescription() {
		LngValidator.GreaterThan instance = new LngValidator.GreaterThan(5L);
		assertEquals("be greater than " + 5L, instance.getTheValueMustDescription());
	}

	@Test
	public void greaterThanGetInvalidMessage() {
		LngValidator.GreaterThan instance = new LngValidator.GreaterThan(5L);
		assertEquals("The value '3' must be greater than 5", instance.getInvalidMessage(3L));
	}

	@Test
	public void greaterThanOrEqualToIsSpecificationValid() {
		LngValidator.GreaterThanOrEqualTo instance = new LngValidator.GreaterThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());

		instance = new LngValidator.GreaterThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void greaterThanOrEqualToGetInvalidSpecificationMessage() {
		LngValidator.GreaterThanOrEqualTo instance = new LngValidator.GreaterThanOrEqualTo(5);
		assertEquals(ALWAYS_VALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void greaterThanOrEqualToIsValid() {
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
	public void greaterThanOrEqualToIsValidThrowsExceptionForNull() {
		final LngValidator.GreaterThanOrEqualTo instance = new LngValidator.GreaterThanOrEqualTo(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void greaterThanOrEqualToGetTheValueMustDescription() {
		final long ref = 5;
		LngValidator.GreaterThanOrEqualTo instance = new LngValidator.GreaterThanOrEqualTo(ref);
		assertEquals("be greater than or equal to " + ref, instance.getTheValueMustDescription());
	}

	@Test
	public void lessThanIsSpecificationValid() {
		LngValidator.LessThan instance = new LngValidator.LessThan(5);
		assertTrue(instance.isSpecificationValid());

		instance = new LngValidator.LessThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void lessThanGetInvalidSpecificationMessage() {
		LngValidator.LessThan instance = new LngValidator.LessThan(5);
		assertEquals(ALWAYS_VALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void lessThanIsValid() {
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
	public void lessThanIsValidThrowsExceptionForNull() {
		final LngValidator.LessThan instance = new LngValidator.LessThan(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void lessThanGetTheValueMustDescription() {
		final long ref = 5;
		LngValidator.LessThan instance = new LngValidator.LessThan(ref);
		assertEquals("be less than " + ref, instance.getTheValueMustDescription());
	}

	@Test
	public void lessThanOrEqualsToIsSpecificationValid() {
		LngValidator.LessThanOrEqualTo instance = new LngValidator.LessThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());

		instance = new LngValidator.LessThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void lessThanOrEqualToGetInvalidSpecificationMessage() {
		LngValidator.LessThanOrEqualTo instance = new LngValidator.LessThanOrEqualTo(5);
		assertEquals(ALWAYS_VALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void lessThanOrEqualToIsValid() {
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

	@Test
	public void lessThanOrEqualToIsValidThrowsExceptionForNull() {
		final LngValidator.LessThanOrEqualTo instance = new LngValidator.LessThanOrEqualTo(5);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void lessThanOrEqualToGetTheValueMustDescription() {
		final long ref = 5;
		LngValidator.LessThanOrEqualTo instance = new LngValidator.LessThanOrEqualTo(ref);
		assertEquals("be less than or equal to " + ref, instance.getTheValueMustDescription());
	}
}
