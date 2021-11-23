package org.yarnandtail.andhow.valid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringValidatorTest {

	public StringValidatorTest() {
	}

	/**
	 * Test of isSpecificationValid method, of class StartsWith.
	 */
	@Test
	public void startsWithIsSpecificationValid() {
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
	public void startsWithIsValid() {
		StringValidator.StartsWith instance = new StringValidator.StartsWith("abc", true);
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("aBcxxx"));
		assertTrue(instance.isValid("ABCXYZ"));
		assertFalse(instance.isValid("\tabc"));
		assertFalse(instance.isValid("cba"));

		instance = new StringValidator.StartsWith("abc", false);
		assertTrue(instance.isValid("abc"));
		assertFalse(instance.isValid("aBc"));
		assertTrue(instance.isValid("abcXYZ"));
		assertFalse(instance.isValid("\tabc"));
		assertFalse(instance.isValid("cba"));
	}

	@Test
	public void startsWithIsValidThrowsExceptionForNull() {
		final StringValidator.StartsWith instance = new StringValidator.StartsWith("abc", true);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	/**
	 * Test of getInvalidSpecificationMessage method, of class StartsWith.
	 */
	@Test
	public void startsWithGetInvalidSpecificationMessageTest() {
		assertEquals("The StartsWith expression cannot be null",
				new StringValidator.StartsWith("A", false).getInvalidSpecificationMessage());
	}

	@Test
	public void startsWithGetTheValueMustDescriptionTest() {
		assertEquals("start with 'A'",
				new StringValidator.StartsWith("A", false).getTheValueMustDescription());
	}

	@Test
	public void startsWithGetInvalidMessage() {
		assertEquals("The value 'B' must start with 'A'",
				new StringValidator.StartsWith("A", false).getInvalidMessage("B"));
	}


	/**
	 * Test of isSpecificationValid method, of class EndsWith.
	 */
	@Test
	public void endsWithIsSpecificationValid() {
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
	public void endsWithIsValid() {
		StringValidator.EndsWith instance = new StringValidator.EndsWith("abc", true);
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("xxxaBc"));
		assertTrue(instance.isValid("XYZABC"));
		assertFalse(instance.isValid("abc\t"));
		assertFalse(instance.isValid("cba"));

		instance = new StringValidator.EndsWith("abc", false);
		assertTrue(instance.isValid("abc"));
		assertFalse(instance.isValid("aBc"));
		assertTrue(instance.isValid("XYZabc"));
		assertFalse(instance.isValid("abc\t"));
		assertFalse(instance.isValid("cba"));
	}

	@Test
	public void endsWithIsValidThrowsExceptionForNull() {
		final StringValidator.EndsWith instance = new StringValidator.EndsWith("abc", true);
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
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
	public void regexIsSpecificationValid() {
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
	public void regexIsValid() {
		StringValidator.Regex instance = new StringValidator.Regex("abc.*");
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("abcXYZ"));
		assertFalse(instance.isValid("cba"));
	}

	@Test
	public void regexIsValidThrowsExceptionForNull() {
		final StringValidator.Regex instance = new StringValidator.Regex("abc.*");
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
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
	 * Test of isSpecificationValid method, of class OneOf.
	 */
	@Test
	public void oneOfIsSpecificationValidTest() {
		StringValidator.OneOf instance = new StringValidator.OneOf((String[]) null);
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.OneOf();
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.OneOf("A", null, "C");
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.OneOf("A");
		assertTrue(instance.isSpecificationValid());

		instance = new StringValidator.OneOf("A", "B", "C");
		assertTrue(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class OneOf.
	 */
	@Test
	public void oneOfIsValidTest() {
		StringValidator.OneOf instance = new StringValidator.OneOf((String) null);
		assertFalse(instance.isValid("A"));

		instance = new StringValidator.OneOf("A");
		assertFalse(instance.isValid("B"));

		instance = new StringValidator.OneOf("A");
		assertFalse(instance.isValid("a"));

		instance = new StringValidator.OneOf("A");
		assertTrue(instance.isValid("A"));

		instance = new StringValidator.OneOf("A", "B", "C");
		assertTrue(instance.isValid("B"));
	}

	@Test
	public void oneOfIsValidThrowsExceptionForNull() {
		final StringValidator.OneOf instance = new StringValidator.OneOf("A");
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	/**
	 * Test of getInvalidSpecificationMessage method, of class OneOf.
	 */
	@Test
	public void oneOfGetInvalidSpecificationMessageTest() {
		assertEquals("The list must contain at least one value and none of the values can be null",
				new StringValidator.OneOf().getInvalidSpecificationMessage());
	}

	/**
	 * Test of getTheValueMustDescriptionTest method, of class OneOf.
	 */
	@Test
	public void oneOfGetTheValueMustDescriptionTest() {
		assertEquals("be equal to one of '[A, B, C]'",
				new StringValidator.OneOf("A", "B", "C").getTheValueMustDescription());
	}

	/**
	 * Test of isSpecificationValid method, of class OneOfIgnoringCase.
	 */
	@Test
	public void oneOfIgnoringCaseIsSpecificationValidTest() {
		StringValidator.OneOfIgnoringCase instance = new StringValidator.OneOfIgnoringCase((String[]) null);
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.OneOfIgnoringCase();
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.OneOfIgnoringCase("A", null, "C");
		assertFalse(instance.isSpecificationValid());

		instance = new StringValidator.OneOfIgnoringCase("A");
		assertTrue(instance.isSpecificationValid());

		instance = new StringValidator.OneOfIgnoringCase("A", "B", "C");
		assertTrue(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class OneOfIgnoringCase.
	 */
	@Test
	public void oneOfIgnoringCaseIsValidTest() {
		StringValidator.OneOfIgnoringCase instance = new StringValidator.OneOfIgnoringCase((String) null);
		assertFalse(instance.isValid("A"));

		instance = new StringValidator.OneOfIgnoringCase("A");
		assertFalse(instance.isValid("b"));

		instance = new StringValidator.OneOfIgnoringCase("A");
		assertTrue(instance.isValid("a"));

		instance = new StringValidator.OneOfIgnoringCase("a");
		assertTrue(instance.isValid("A"));

		instance = new StringValidator.OneOfIgnoringCase("A", "B", "C");
		assertTrue(instance.isValid("b"));
	}

	@Test
	public void oneOfIgnoringCaseIsValidThrowsExceptionForNull() {
		final StringValidator.OneOfIgnoringCase instance = new StringValidator.OneOfIgnoringCase("A");
		assertThrows(IllegalArgumentException.class, () -> instance.isValid(null));
	}

	/**
	 * Test of getInvalidSpecificationMessage method, of class OneOfIgnoringCase.
	 */
	@Test
	public void oneOfIgnoringCaseGetInvalidSpecificationMessageTest() {
		assertEquals("The list must contain at least one value and none of the values can be null",
				new StringValidator.OneOfIgnoringCase().getInvalidSpecificationMessage());
	}

	/**
	 * Test of getTheValueMustDescriptionTest method, of class OneOf.
	 */
	@Test
	public void oneOfIgnoringCaseGetTheValueMustDescriptionTest() {
		assertEquals("be equal to one of '[A, B, C]' ignoring case",
				new StringValidator.OneOfIgnoringCase("A", "B", "C").getTheValueMustDescription());
	}
}
