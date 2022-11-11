package org.yarnandtail.andhow.junit5.usagetests;

import org.junit.jupiter.api.*;
import org.yarnandtail.andhow.junit5.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This classes annotation BeforeAll...Deb should effectively be ignored b/c the superclass
 * BeforeEach will re-initialize AndHow before each method.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ConfigFromFileBeforeAllTests(value = "Conf1OnlyAsDeb.properties", includeClasses = {Conf1.class})
class ConfigFromFileMixedUsage3Test extends EachAsBobBase {

	@BeforeAll
	public static void setUp() {
		// There is a brief window between BeforeAll and BeforeEach where AndHow is configured for 'Deb'
		assertEquals("Deb", Conf1.MY_PROP.getValue());
	}

	@Order(1)
	@Test
	public void test1() {
		assertEquals("Bob", Conf1.MY_PROP.getValue());
	}

	// This test does NOT pick up the annotations of the test it overrides
	@Order(2)
	@Test
	@Override
	public void test2() {
		assertEquals("Bob", Conf1.MY_PROP.getValue());
	}

	@Order(4)
	@Test
	public void test4() {
		assertEquals("Bob", Conf1.MY_PROP.getValue());
	}

	@Nested
	class Inner1 {

		@Test
		public void test1() {
			assertEquals("Bob", Conf1.MY_PROP.getValue());
		}
	}

	@Nested
	class Inner2 extends EachAsCarlBase {

		@Test
		public void test1() {
			assertEquals("Carl", Conf1.MY_PROP.getValue());
		}
	}

	/**
	 * Effectively override the 'Each' inherited from the parent.
	 */
	@ConfigFromFileBeforeEachTest(value = "Conf1OnlyAsDeb.properties", includeClasses = {Conf1.class})
	@Nested
	class Inner3 extends EachAsCarlBase {

		@Test
		public void test1() {
			assertEquals("Deb", Conf1.MY_PROP.getValue());
		}
	}

	/**
	 * This BeforeAll is effectively ignored just like the one on the parent class
	 */
	@ConfigFromFileBeforeAllTests(value = "Conf1OnlyAsDeb.properties", includeClasses = {Conf1.class})
	@Nested
	class Inner4 {

		@Test
		public void test1() {
			assertEquals("Bob", Conf1.MY_PROP.getValue());
		}
	}

}