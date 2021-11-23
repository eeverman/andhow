package org.yarnandtail.andhow.valid;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class LocalDateTimeValidatorTest {

	private static final String INVALID_SPECIFICATION_MESSAGE = "The reference LocalDateTime cannot be null";

	LocalDateTime DEC_03_2007_AT_5PM = LocalDateTime.parse("2007-12-03T17:00");

	@Test
	public void before() {
		LocalDateTimeValidator.Before instance = new LocalDateTimeValidator.Before(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());

		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)), "One nano second before");
		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.DAYS)), "One day second before");

		assertFalse(instance.isValid(DEC_03_2007_AT_5PM), "Same date should not be valid");
		assertFalse(instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)), "One nano after should not be valid");

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be before 2007-12-03T17:00", instance.getTheValueMustDescription());
	}

	@Test
	public void beforeIsValidThrowsExceptionForNull() {
		final LocalDateTimeValidator.Before instance = new LocalDateTimeValidator.Before(DEC_03_2007_AT_5PM);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void sameTimeOrBefore() {
		LocalDateTimeValidator.SameTimeOrBefore instance = new LocalDateTimeValidator.SameTimeOrBefore(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());

		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)), "One nano second before");
		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.DAYS)), "One day second before");
		assertTrue(instance.isValid(DEC_03_2007_AT_5PM), "Same date should be valid");

		assertFalse(instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)), "One nano after should not be valid");

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be the same date and time or before 2007-12-03T17:00", instance.getTheValueMustDescription());
	}

	@Test
	public void sameTimeOrBeforeIsValidThrowsExceptionForNull() {
		final LocalDateTimeValidator.SameTimeOrBefore instance = new LocalDateTimeValidator.SameTimeOrBefore(DEC_03_2007_AT_5PM);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void after() {
		LocalDateTimeValidator.After instance = new LocalDateTimeValidator.After(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());

		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)), "One nano second after");
		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.DAYS)), "One day second before");

		assertFalse(instance.isValid(DEC_03_2007_AT_5PM), "Same date should not be valid");
		assertFalse(instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)), "One nano before should not be valid");

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be after 2007-12-03T17:00", instance.getTheValueMustDescription());
	}

	@Test
	public void afterIsValidThrowsExceptionForNull() {
		final LocalDateTimeValidator.After instance = new LocalDateTimeValidator.After(DEC_03_2007_AT_5PM);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	@Test
	public void sameTimeOrAfter() {
		LocalDateTimeValidator.SameTimeOrAfter instance = new LocalDateTimeValidator.SameTimeOrAfter(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());

		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)), "One nano second after");
		assertTrue(instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.DAYS)), "One day second after");
		assertTrue(instance.isValid(DEC_03_2007_AT_5PM), "Same date should be valid");

		assertFalse(instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)), "One nano before should not be valid");

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be the same date and time or after 2007-12-03T17:00", instance.getTheValueMustDescription());
	}

	@Test
	public void sameTimeOrAfterIsValidThrowsExceptionForNull() {
		final LocalDateTimeValidator.SameTimeOrAfter instance = new LocalDateTimeValidator.SameTimeOrAfter(DEC_03_2007_AT_5PM);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	//
	// Less Happy path-y

	@Test
	public void invalidSpec() {
		LocalDateTimeValidator.SameTimeOrAfter instance = new LocalDateTimeValidator.SameTimeOrAfter(null);
		assertFalse(instance.isSpecificationValid());
	}

}
