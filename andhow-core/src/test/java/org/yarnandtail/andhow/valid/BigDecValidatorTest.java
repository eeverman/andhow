package org.yarnandtail.andhow.valid;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BigDecValidatorTest {

	private static String EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE = "The constraint may not be null";
	private static final BigDecimal FOUR = new BigDecimal("4.5678");
	private static final BigDecimal FIVE = new BigDecimal("5.12345");
	private static final BigDecimal SIX = new BigDecimal("6.452134563456");
	private static final BigDecimal NEGATIVE_NINETY_NINE = new BigDecimal("99.45623").negate();

	@Test
	public void greaterThan_IsSpecificationValid() {
		BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.GreaterThan(new BigDecimal("-12.8765"));
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.GreaterThan(null);
		assertFalse(instance.isSpecificationValid());
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void greaterThan_GetTheValueMustDescription() {
		BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
		assertEquals("be greater than " + FIVE, instance.getTheValueMustDescription());
	}

	@Test
	public void greaterThan_IsValid() {
		BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
		assertFalse(instance.isValid(FOUR));
		assertFalse(instance.isValid(FIVE));
		assertTrue(instance.isValid(SIX));
	}

	@Test
	public void greaterThan_IsValidThrowsExceptionForNull() {
		final BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void greaterThanOrEqualTo_IsSpecificationValid() {
		BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.GreaterThanOrEqualTo(new BigDecimal("-999999999"));
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.GreaterThanOrEqualTo(null);
		assertFalse(instance.isSpecificationValid());
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void greaterThanOrEqualTo_GetTheValueMustDescription() {
		BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
		assertEquals("be greater than or equal to " + FIVE, instance.getTheValueMustDescription());
	}

	@Test
	public void greaterThanOrEqualTo_IsValid() {
		BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
		assertFalse(instance.isValid(FOUR));
		assertTrue(instance.isValid(FIVE));
		assertTrue(instance.isValid(SIX));
	}

	@Test
	public void greaterThanOrEqualTo_IsValidThrowsExceptionForNull() {
		final BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}


	@Test
	public void lessThan_IsSpecificationValid() {
		BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.LessThan(NEGATIVE_NINETY_NINE);
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.LessThan(null);
		assertFalse(instance.isSpecificationValid());
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void lessThan_GetTheValueMustDescription() {
		BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
		assertEquals("be less than " + FIVE, instance.getTheValueMustDescription());
	}

	@Test
	public void lessThan_IsValid() {
		BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
		assertTrue(instance.isValid(FOUR));
		assertFalse(instance.isValid(FIVE));
		assertFalse(instance.isValid(SIX));
	}

	@Test
	public void lessThan_IsValidThrowsExceptionForNull() {
		final BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void testLessThanOrEqualsTo_IsSpecificationValid() {
		BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.LessThanOrEqualTo(NEGATIVE_NINETY_NINE);
		assertTrue(instance.isSpecificationValid());

		instance = new BigDecValidator.LessThanOrEqualTo(null);
		assertFalse(instance.isSpecificationValid());
		assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
	}

	@Test
	public void lessThanOrEqualTo_GetTheValueMustDescription() {
		BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
		assertEquals("be less than or equal to " + FIVE, instance.getTheValueMustDescription());
	}

	@Test
	public void lessThanOrEqualTo_IsValid() {
		BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
		assertTrue(instance.isValid(FOUR));
		assertTrue(instance.isValid(FIVE));
		assertFalse(instance.isValid(SIX));
	}

	@Test
	public void lessThanOrEqualTo_IsValidThrowsExceptionForNull() {
		final BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}
}
