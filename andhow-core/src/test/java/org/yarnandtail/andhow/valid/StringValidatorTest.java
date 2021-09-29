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
	 * Test of getInvalidSpecificationMessage method, of class StartsWith.
	 */
	@Test
	public void startsWithGetInvalidSpecificationMessageTest() {
		assertEquals("The StartsWith expression cannot be null",
				new StringValidator.StartsWith("A", false).getInvalidSpecificationMessage());
	}

	/**
	 * Test of getInvalidSpecificationMessage method, of class StartsWith.
	 */
	@Test
	public void startsWithGetTheValueMustDescriptionTest() {
		assertEquals("start with 'A'",
				new StringValidator.StartsWith("A", false).getTheValueMustDescription());
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
	 * Test of getInvalidSpecificationMessage method, of class EndsWith.
	 */
	@Test
	public void endsWithGetInvalidSpecificationMessageTest() {
		assertEquals("The EndWith expression cannot be null",
				new StringValidator.EndsWith("A", false).getInvalidSpecificationMessage());
	}

	/**
	 * Test of getInvalidSpecificationMessage method, of class EndsWith.
	 */
	@Test
	public void endsWithGetTheValueMustDescriptionTest() {
		assertEquals("end with 'A'",
				new StringValidator.EndsWith("A", false).getTheValueMustDescription());
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
	 * Test of getInvalidSpecificationMessage method, of class Regex.
	 */
	@Test
	public void regexGetInvalidSpecificationMessageTest() {
		assertEquals("The expression 'abc.*[' is not a valid regex expression",
				new StringValidator.Regex("abc.*[").getInvalidSpecificationMessage());
	}

	/**
	 * Test of getTheValueMustDescriptionTest method, of class Regex.
	 */
	@Test
	public void regexGetTheValueMustDescriptionTest() {
		assertEquals("match the regex expression 'abc*'",
				new StringValidator.Regex("abc*").getTheValueMustDescription());
	}

	/**
	 * Test of isSpecificationValid method, of class Equals.
	 */
	@Test
	public void equalsIsSpecificationValidTest() {
		StringValidator.Equals instance = new StringValidator.Equals((String) null);
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.Equals();
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.Equals("A", null, "C");
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.Equals("A");
		assertTrue(instance.isSpecificationValid());

		instance = new StringValidator.Equals("A", "B", "C");
		assertTrue(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class Equals.
	 */
	@Test
	public void equalsIsValidTest() {
		StringValidator.Equals instance = new StringValidator.Equals((String) null);
		assertFalse(instance.isValid("A"));

		instance = new StringValidator.Equals("A");
		assertFalse(instance.isValid(null));

		instance = new StringValidator.Equals("A");
		assertFalse(instance.isValid("B"));

		instance = new StringValidator.Equals("A");
		assertFalse(instance.isValid("a"));

		instance = new StringValidator.Equals("A");
		assertTrue(instance.isValid("A"));

		instance = new StringValidator.Equals("A", "B", "C");
		assertTrue(instance.isValid("B"));
	}

	/**
	 * Test of getInvalidSpecificationMessage method, of class Equals.
	 */
	@Test
	public void equalsGetInvalidSpecificationMessageTest() {
		assertEquals("The Equals list must contain at least one value",
				new StringValidator.Equals().getInvalidSpecificationMessage());
	}

	/**
	 * Test of getTheValueMustDescriptionTest method, of class Equals.
	 */
	@Test
	public void equalsGetTheValueMustDescriptionTest() {
		StringValidator.Equals instance = new StringValidator.Equals("A", "B", "C");
		assertEquals("be equal to one of '[A, B, C]'", instance.getTheValueMustDescription());
	}
}
