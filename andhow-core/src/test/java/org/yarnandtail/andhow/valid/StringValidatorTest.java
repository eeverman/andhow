package org.yarnandtail.andhow.valid;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author ericeverman
 */
public class StringValidatorTest {
	
	public StringValidatorTest() {
	}
	
	/**
	 * Test of isSpecificationValid method, of class StartsWith.
	 */
	@Test
	public void testStartsWithIsSpecificationValid() {
		StringValidator.StartsWith instance = new StringValidator.StartsWith("abc", true);
		assertTrue(instance.isSpecificationValid());
		
		instance = new StringValidator.StartsWith("\t", false);
		assertTrue(instance.isSpecificationValid());
		
		instance = new StringValidator.StartsWith(null, true);
		assertFalse(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class StartsWith.
	 */
	@Test
	public void testStartsWithIsValid() {
		StringValidator.StartsWith instance = new StringValidator.StartsWith("abc", true);
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("aBcxxx"));
		assertTrue(instance.isValid("ABCXYZ"));
		assertFalse(instance.isValid("\tabc"));
		assertFalse(instance.isValid("cba"));
		assertFalse(instance.isValid(null));
		
		instance = new StringValidator.StartsWith("abc", false);
		assertTrue(instance.isValid("abc"));
		assertFalse(instance.isValid("aBc"));
		assertTrue(instance.isValid("abcXYZ"));
		assertFalse(instance.isValid("\tabc"));
		assertFalse(instance.isValid("cba"));
		assertFalse(instance.isValid(null));
	}
	

	/**
	 * Test of isSpecificationValid method, of class EndsWith.
	 */
	@Test
	public void testEndsWithIsSpecificationValid() {
		StringValidator.EndsWith instance = new StringValidator.EndsWith("abc", true);
		assertTrue(instance.isSpecificationValid());
		
		instance = new StringValidator.EndsWith("\t", false);
		assertTrue(instance.isSpecificationValid());
		
		instance = new StringValidator.EndsWith(null, true);
		assertFalse(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class EndsWith.
	 */
	@Test
	public void testEndsWithIsValid() {
		StringValidator.EndsWith instance = new StringValidator.EndsWith("abc", true);
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("xxxaBc"));
		assertTrue(instance.isValid("XYZABC"));
		assertFalse(instance.isValid("abc\t"));
		assertFalse(instance.isValid("cba"));
		assertFalse(instance.isValid(null));
		
		instance = new StringValidator.EndsWith("abc", false);
		assertTrue(instance.isValid("abc"));
		assertFalse(instance.isValid("aBc"));
		assertTrue(instance.isValid("XYZabc"));
		assertFalse(instance.isValid("abc\t"));
		assertFalse(instance.isValid("cba"));
		assertFalse(instance.isValid(null));
	}
	
	

	/**
	 * Test of isSpecificationValid method, of class StringRegex.
	 */
	@Test
	public void testRegexIsSpecificationValid() {
		StringValidator.Regex instance = new StringValidator.Regex("abc.*");
		assertTrue(instance.isSpecificationValid());
		
		instance = new StringValidator.Regex("abc.*[");
		assertFalse(instance.isSpecificationValid());
		
		instance = new StringValidator.Regex(null);
		assertFalse(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class StringRegex.
	 */
	@Test
	public void testRegexIsValid() {
		StringValidator.Regex instance = new StringValidator.Regex("abc.*");
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("abcXYZ"));
		assertFalse(instance.isValid("cba"));
		assertFalse(instance.isValid(null));
	}

	/**
	 * Test of isSpecificationValid method, of class Equals.
	 */
	@Test
	public void nullEntriesIsSpecificationValidTest() {
		StringValidator.Equals instance = new StringValidator.Equals("A", null, "C");

		assertFalse(instance.isSpecificationValid());
	}
}
