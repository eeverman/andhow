package yarnandtail.andhow.valid;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class StringValidatorTest {
	
	public StringValidatorTest() {
	}
	

	/**
	 * Test of isSpecificationValid method, of class StringRegex.
	 */
	@Test
	public void testIsSpecificationValid() {
		StringValidator.Regex instance = new StringValidator.Regex("abc.*");
		assertTrue(instance.isSpecificationValid());
		
		instance = new StringValidator.Regex("abc.*[");
		assertFalse(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class StringRegex.
	 */
	@Test
	public void testIsValid() {
		StringValidator.Regex instance = new StringValidator.Regex("abc.*");
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("abcXYZ"));
		assertFalse(instance.isValid("cba"));
	}
	
}
