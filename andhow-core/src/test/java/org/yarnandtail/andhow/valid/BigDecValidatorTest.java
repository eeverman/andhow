package org.yarnandtail.andhow.valid;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BigDecValidatorTest {

    private static String EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE = "THIS VALIDATION IS ALWAYS VALID";
    private static final BigDecimal FOUR = new BigDecimal("4.5678");
    private static final BigDecimal FIVE = new BigDecimal("5.12345");
    private static final BigDecimal SIX = new BigDecimal("6.452134563456");
    private static final BigDecimal NEGATIVE_NINETY_NINE = new BigDecimal("99.45623").negate();

    @Test
    public void testGreaterThan_IsSpecificationValid() {
        BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThan(new BigDecimal("-12.8765"));
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThan(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testGreaterThan_GetTheValueMustDescription() {
        BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
        assertEquals("be greater than " + FIVE, instance.getTheValueMustDescription());
    }

    @Test
    public void testGreaterThan_IsValid() {
        BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
        assertFalse(instance.isValid(FOUR));
        assertFalse(instance.isValid(FIVE));
        assertTrue(instance.isValid(SIX));
        assertFalse(instance.isValid(null));
    }

    @Test
    public void testGreaterThanOrEqualTo_IsSpecificationValid() {
        BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThanOrEqualTo(new BigDecimal("-999999999"));
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThanOrEqualTo(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testGreaterThanOrEqualTo_GetTheValueMustDescription() {
        BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
        assertEquals("be greater than or equal to " + FIVE, instance.getTheValueMustDescription());
    }

    @Test
    public void testGreaterThanOrEqualTo_IsValid() {
        BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
        assertFalse(instance.isValid(FOUR));
        assertTrue(instance.isValid(FIVE));
        assertTrue(instance.isValid(SIX));
        assertFalse(instance.isValid(null));
    }


    @Test
    public void testLessThan_IsSpecificationValid() {
        BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThan(NEGATIVE_NINETY_NINE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThan(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testLessThan_GetTheValueMustDescription() {
        BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
        assertEquals("be less than " + FIVE, instance.getTheValueMustDescription());
    }

    @Test
    public void testLessThan_IsValid() {
        BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
        assertTrue(instance.isValid(FOUR));
        assertFalse(instance.isValid(FIVE));
        assertFalse(instance.isValid(SIX));
        assertFalse(instance.isValid(null));
    }

    @Test
    public void testLessThanOrEqualsTo_IsSpecificationValid() {
        BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThanOrEqualTo(NEGATIVE_NINETY_NINE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThanOrEqualTo(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testLessThanOrEqualTo_GetTheValueMustDescription() {
        BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
        assertEquals("be less than or equal to " + FIVE, instance.getTheValueMustDescription());
    }

    @Test
    public void testLessThanOrEqualTo_IsValid() {
        BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
        assertTrue(instance.isValid(FOUR));
        assertTrue(instance.isValid(FIVE));
        assertFalse(instance.isValid(SIX));
        assertFalse(instance.isValid(null));
    }

    @Test
    public void testGreaterThan_InvalidSpecificationMessage() {
        BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testGreaterThanOrEqualTo_InvalidSpecificationMessage() {
        BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testLessThan_InvalidSpecificationMessage() {
        BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testLessThanOrEqualTo_InvalidSpecificationMessage() {
        BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }
}
