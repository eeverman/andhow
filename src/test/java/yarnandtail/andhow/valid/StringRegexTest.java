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
public class StringRegexTest {
	
	public StringRegexTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of isSpecificationValid method, of class StringRegex.
	 */
	@Test
	public void testIsSpecificationValid() {
		StringRegex instance = new StringRegex("abc.*");
		assertTrue(instance.isSpecificationValid());
		
		instance = new StringRegex("abc.*[");
		assertFalse(instance.isSpecificationValid());
	}

	/**
	 * Test of isValid method, of class StringRegex.
	 */
	@Test
	public void testIsValid() {
		StringRegex instance = new StringRegex("abc.*");
		assertTrue(instance.isValid("abc"));
		assertTrue(instance.isValid("abcXYZ"));
		assertFalse(instance.isValid("cba"));
	}
	
}
