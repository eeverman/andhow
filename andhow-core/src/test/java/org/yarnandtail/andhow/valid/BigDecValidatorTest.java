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
    private static final BigDecimal NEGATIVE_ONE_HUNDRED = new BigDecimal("100.34532").negate();
    private static final BigDecimal NEGATIVE_NINETY_NINE = new BigDecimal("99.45623").negate();
    private static final BigDecimal NEGATIVE_NINETY_EIGHT = new BigDecimal("98.1234").negate();

    @Test
    public void testGreaterThanIsSpecificationValid() {
        BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThan(new BigDecimal("-12.8765"));
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThan(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testGreaterThanIsValid() {
        BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
        assertFalse(instance.isValid(FOUR));
        assertFalse(instance.isValid(FIVE));
        assertTrue(instance.isValid(SIX));
        assertFalse(instance.isValid(null));

        instance = new BigDecValidator.GreaterThan(BigDecimal.ZERO);
        assertFalse(instance.isValid(BigDecimal.ONE.negate()));
        assertFalse(instance.isValid(BigDecimal.ZERO));
        assertTrue(instance.isValid(BigDecimal.ONE));

        instance = new BigDecValidator.GreaterThan(NEGATIVE_NINETY_NINE);
        assertFalse(instance.isValid(NEGATIVE_ONE_HUNDRED));
        assertFalse(instance.isValid(NEGATIVE_NINETY_NINE));
        assertTrue(instance.isValid(NEGATIVE_NINETY_EIGHT));
    }

    @Test
    public void testGreaterThanOrEqualToIsSpecificationValid() {
        BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThanOrEqualTo(new BigDecimal("-999999999"));
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.GreaterThanOrEqualTo(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testGreaterThanOrEqualToIsValid() {
        BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
        assertFalse(instance.isValid(FOUR));
        assertTrue(instance.isValid(FIVE));
        assertTrue(instance.isValid(SIX));
        assertFalse(instance.isValid(null));

        instance = new BigDecValidator.GreaterThanOrEqualTo(BigDecimal.ZERO);
        assertFalse(instance.isValid(BigDecimal.ONE.negate()));
        assertTrue(instance.isValid(BigDecimal.ZERO));
        assertTrue(instance.isValid(BigDecimal.ONE));


        instance = new BigDecValidator.GreaterThanOrEqualTo(NEGATIVE_NINETY_NINE);
        assertFalse(instance.isValid(NEGATIVE_ONE_HUNDRED));
        assertTrue(instance.isValid(NEGATIVE_NINETY_NINE));
        assertTrue(instance.isValid(NEGATIVE_NINETY_EIGHT));
    }


    @Test
    public void testLessThanIsSpecificationValid() {
        BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThan(NEGATIVE_NINETY_NINE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThan(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testLessThanIsValid() {
        BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
        assertTrue(instance.isValid(FOUR));
        assertFalse(instance.isValid(FIVE));
        assertFalse(instance.isValid(SIX));
        assertFalse(instance.isValid(null));

        instance = new BigDecValidator.LessThan(BigDecimal.ZERO);
        assertTrue(instance.isValid(BigDecimal.ONE.negate()));
        assertFalse(instance.isValid(BigDecimal.ZERO));
        assertFalse(instance.isValid(BigDecimal.ONE));

        instance = new BigDecValidator.LessThan(NEGATIVE_NINETY_NINE);
        assertTrue(instance.isValid(NEGATIVE_ONE_HUNDRED));
        assertFalse(instance.isValid(NEGATIVE_NINETY_NINE));
        assertFalse(instance.isValid(NEGATIVE_NINETY_EIGHT));
    }

    @Test
    public void testLessThanOrEqualsToIsSpecificationValid() {
        BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThanOrEqualTo(NEGATIVE_NINETY_NINE);
        assertTrue(instance.isSpecificationValid());

        instance = new BigDecValidator.LessThanOrEqualTo(null);
        assertFalse(instance.isSpecificationValid());
    }

    @Test
    public void testLessThanOrEqualToIsValid() {
        BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
        assertTrue(instance.isValid(FOUR));
        assertTrue(instance.isValid(FIVE));
        assertFalse(instance.isValid(SIX));
        assertFalse(instance.isValid(null));

        instance = new BigDecValidator.LessThanOrEqualTo(BigDecimal.ZERO);
        assertTrue(instance.isValid(BigDecimal.ONE.negate()));
        assertTrue(instance.isValid(BigDecimal.ZERO));
        assertFalse(instance.isValid(BigDecimal.ONE));

        instance = new BigDecValidator.LessThanOrEqualTo(NEGATIVE_NINETY_NINE);
        assertTrue(instance.isValid(NEGATIVE_ONE_HUNDRED));
        assertTrue(instance.isValid(NEGATIVE_NINETY_NINE));
        assertFalse(instance.isValid(NEGATIVE_NINETY_EIGHT));

    }

    @Test
    public void testGreaterThanInvalidSpecificationMessage() {
        BigDecValidator.GreaterThan instance = new BigDecValidator.GreaterThan(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testGreaterThanOrEqualToInvalidSpecificationMessage() {
        BigDecValidator.GreaterThanOrEqualTo instance = new BigDecValidator.GreaterThanOrEqualTo(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testLessThanInvalidSpecificationMessage() {
        BigDecValidator.LessThan instance = new BigDecValidator.LessThan(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }

    @Test
    public void testLessThanOrEqualToInvalidSpecificationMessage() {
        BigDecValidator.LessThanOrEqualTo instance = new BigDecValidator.LessThanOrEqualTo(FIVE);
        assertEquals(EXPECTED_DBL_VALIDATOR_INVALID_MESSAGE, instance.getInvalidSpecificationMessage());
    }
}
