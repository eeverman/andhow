package org.yarnandtail.andhow.valid;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ericeverman
 */
public class LocalDateTimeValidatorTest {

	private static final String INVALID_SPECIFICATION_MESSAGE = "The reference LocalDateTime cannot be null";

	LocalDateTime DEC_03_2007_AT_5PM =  LocalDateTime.parse("2007-12-03T17:00");

	@Test
	public void testBefore() {
		LocalDateTimeValidator.Before instance = new LocalDateTimeValidator.Before(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());
		
		assertTrue("One nano second before", instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)));
		assertTrue("One day second before", instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.DAYS)));
		
		assertFalse("Same date should not be valid", instance.isValid(DEC_03_2007_AT_5PM));
		assertFalse("One nano after should not be valid", instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)));
		assertFalse("Null should not be valid", instance.isValid(null));

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be before 2007-12-03T17:00", instance.getTheValueMustDescription());
	}

	@Test
	public void testSameTimeOrBefore() {
		LocalDateTimeValidator.SameTimeOrBefore instance = new LocalDateTimeValidator.SameTimeOrBefore(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());
		
		assertTrue("One nano second before", instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)));
		assertTrue("One day second before", instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.DAYS)));
		assertTrue("Same date should be valid", instance.isValid(DEC_03_2007_AT_5PM));
		
		assertFalse("One nano after should not be valid", instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)));
		assertFalse("Null should not be valid", instance.isValid(null));

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be the same date and time or before 2007-12-03T17:00", instance.getTheValueMustDescription());
	}
	
	@Test
	public void testAfter() {
		LocalDateTimeValidator.After instance = new LocalDateTimeValidator.After(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());
		
		assertTrue("One nano second after", instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)));
		assertTrue("One day second before", instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.DAYS)));
		
		assertFalse("Same date should not be valid", instance.isValid(DEC_03_2007_AT_5PM));
		assertFalse("One nano before should not be valid", instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)));
		assertFalse("Null should not be valid", instance.isValid(null));

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be after 2007-12-03T17:00", instance.getTheValueMustDescription());
	}
	
	@Test
	public void testSameTimeOrAfter() {
		LocalDateTimeValidator.SameTimeOrAfter instance = new LocalDateTimeValidator.SameTimeOrAfter(DEC_03_2007_AT_5PM);
		assertTrue(instance.isSpecificationValid());
		
		assertTrue("One nano second after", instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.NANOS)));
		assertTrue("One day second after", instance.isValid(DEC_03_2007_AT_5PM.plus(1, ChronoUnit.DAYS)));
		assertTrue("Same date should be valid", instance.isValid(DEC_03_2007_AT_5PM));
		
		assertFalse("One nano before should not be valid", instance.isValid(DEC_03_2007_AT_5PM.minus(1, ChronoUnit.NANOS)));
		assertFalse("Null should not be valid", instance.isValid(null));

		assertEquals(INVALID_SPECIFICATION_MESSAGE, instance.getInvalidSpecificationMessage());

		assertEquals("be the same date and time or after 2007-12-03T17:00", instance.getTheValueMustDescription());
	}
	
	//
	// Less Happy path-y
	
	@Test
	public void testInvalidSpec() {
		LocalDateTimeValidator.SameTimeOrAfter instance = new LocalDateTimeValidator.SameTimeOrAfter(null);
		assertFalse(instance.isSpecificationValid());
	}
	
}
