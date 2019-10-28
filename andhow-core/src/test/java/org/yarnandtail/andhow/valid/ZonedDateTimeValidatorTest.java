package org.yarnandtail.andhow.valid;

import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class ZonedDateTimeValidatorTest {

    private static String EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE = "The constraint may not be null";
    private static final ZonedDateTime CURRENT_TIME = ZonedDateTime.now();

    @Test
    public void testGreaterThan_IsSpecificationValid() {
        ZonedDateTimeValidator.GreaterThan instance = new ZonedDateTimeValidator.GreaterThan(CURRENT_TIME);
        assertTrue(instance.isSpecificationValid());

        instance = new ZonedDateTimeValidator.GreaterThan(null);
        assertFalse(instance.isSpecificationValid());
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testGreaterThan_GetTheValueMustDescription() {
        ZonedDateTimeValidator.GreaterThan instance = new ZonedDateTimeValidator.GreaterThan(CURRENT_TIME);
        assertEquals("be greater than " + CURRENT_TIME, instance.getTheValueMustDescription());
    }

    @Test
    public void testGreaterThan_IsValid() {
        ZonedDateTimeValidator.GreaterThan instance = new ZonedDateTimeValidator.GreaterThan(CURRENT_TIME);
        assertFalse(instance.isValid(CURRENT_TIME));
        assertFalse(instance.isValid(CURRENT_TIME.minus(1, ChronoUnit.DAYS)));
        assertTrue(instance.isValid(CURRENT_TIME.plus(1, ChronoUnit.DAYS)));
        assertFalse(instance.isValid(null));
    }

    @Test
    public void testGreaterThanOrEqualTo_IsSpecificationValid() {
        ZonedDateTimeValidator.GreaterThanOrEqualTo instance = new ZonedDateTimeValidator.GreaterThanOrEqualTo(CURRENT_TIME);
        assertTrue(instance.isSpecificationValid());

        instance = new ZonedDateTimeValidator.GreaterThanOrEqualTo(null);
        assertFalse(instance.isSpecificationValid());
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testGreaterThanOrEqualTo_GetTheValueMustDescription() {
        ZonedDateTimeValidator.GreaterThanOrEqualTo instance = new ZonedDateTimeValidator.GreaterThanOrEqualTo(CURRENT_TIME);
        assertEquals("be greater than or equal to " + CURRENT_TIME, instance.getTheValueMustDescription());
    }

    @Test
    public void testGreaterThanOrEqualTo_IsValid() {
        ZonedDateTimeValidator.GreaterThanOrEqualTo instance = new ZonedDateTimeValidator.GreaterThanOrEqualTo(CURRENT_TIME);
        assertTrue(instance.isValid(CURRENT_TIME));
        assertFalse(instance.isValid(CURRENT_TIME.minus(1, ChronoUnit.DAYS)));
        assertTrue(instance.isValid(CURRENT_TIME.plus(1, ChronoUnit.DAYS)));
        assertFalse(instance.isValid(null));
    }

    @Test
    public void testLessThan_IsSpecificationValid() {
        ZonedDateTimeValidator.LessThan instance = new ZonedDateTimeValidator.LessThan(CURRENT_TIME);
        assertTrue(instance.isSpecificationValid());

        instance = new ZonedDateTimeValidator.LessThan(null);
        assertFalse(instance.isSpecificationValid());
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testLessThan_GetTheValueMustDescription() {
        ZonedDateTimeValidator.LessThan instance = new ZonedDateTimeValidator.LessThan(CURRENT_TIME);
        assertEquals("be less than " + CURRENT_TIME, instance.getTheValueMustDescription());
    }

    @Test
    public void testLessThan_IsValid() {
        ZonedDateTimeValidator.LessThan instance = new ZonedDateTimeValidator.LessThan(CURRENT_TIME);
        assertFalse(instance.isValid(CURRENT_TIME));
        assertTrue(instance.isValid(CURRENT_TIME.minus(1, ChronoUnit.DAYS)));
        assertFalse(instance.isValid(CURRENT_TIME.plus(1, ChronoUnit.DAYS)));
        assertFalse(instance.isValid(null));
    }
}
