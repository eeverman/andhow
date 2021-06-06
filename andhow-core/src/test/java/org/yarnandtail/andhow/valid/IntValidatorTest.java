package org.yarnandtail.andhow.valid;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author ericeverman
 */
public class IntValidatorTest {

	@Test
	public void testGreaterThanIsSpecificationValid() {
		IntValidator.GreaterThan instance = new IntValidator.GreaterThan(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new IntValidator.GreaterThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testGreaterThanIsValid() {
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
	public void testGreaterThanOrEqualToIsSpecificationValid() {
		IntValidator.GreaterThanOrEqualTo instance = new IntValidator.GreaterThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new IntValidator.GreaterThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testGreaterThanOrEqualToIsValid() {
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
	public void testLessThanIsSpecificationValid() {
		IntValidator.LessThan instance = new IntValidator.LessThan(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new IntValidator.LessThan(-999999999);
		assertTrue(instance.isSpecificationValid());
	}

	@Test
	public void testLessThanIsValid() {
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
	public void testLessThanOrEqualsToIsSpecificationValid() {
		IntValidator.LessThanOrEqualTo instance = new IntValidator.LessThanOrEqualTo(5);
		assertTrue(instance.isSpecificationValid());
		
		instance = new IntValidator.LessThanOrEqualTo(-999999999);
		assertTrue(instance.isSpecificationValid());
	}
	
	@Test
	public void testLessThanOrEqualToIsValid() {
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
    public void testNullValidation() {
        IntValidator.GreaterThan greaterThan = new IntValidator.GreaterThan(4);
        assertFalse(greaterThan.isValid(null));

        IntValidator.GreaterThanOrEqualTo greaterThanOrEqualTo = new IntValidator.GreaterThanOrEqualTo(4);
        assertFalse(greaterThanOrEqualTo.isValid(null));

        IntValidator.LessThan lessThan = new IntValidator.LessThan(4);
        assertFalse(lessThan.isValid(null));

        IntValidator.LessThanOrEqualTo lessThanOrEqualTo = new IntValidator.LessThanOrEqualTo(4);
        assertFalse(lessThanOrEqualTo.isValid(null));
    }


    @Test
    public void testInvalidMessage() {
        IntValidator.GreaterThan greaterThan = new IntValidator.GreaterThan(4);
        assertEquals("be greater than 4", greaterThan.getTheValueMustDescription());

        IntValidator.GreaterThanOrEqualTo greaterThanOrEqualTo = new IntValidator.GreaterThanOrEqualTo(4);
        assertEquals("be greater than or equal to 4", greaterThanOrEqualTo.getTheValueMustDescription());

        IntValidator.LessThan lessThan = new IntValidator.LessThan(4);
        assertEquals("be less than 4", lessThan.getTheValueMustDescription());

        IntValidator.LessThanOrEqualTo lessThanOrEqualTo = new IntValidator.LessThanOrEqualTo(4);
        assertEquals("be less than or equal to 4", lessThanOrEqualTo.getTheValueMustDescription());
    }

    @Test
    public void testInvalidSpecificationMessage(){
        IntValidator.GreaterThan greaterThan = new IntValidator.GreaterThan(4);
        assertEquals("THIS VALIDATION IS ALWAYS VALID", greaterThan.getInvalidSpecificationMessage());
    }
}
