package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test order is important here because we want to verify that Sys Props are
 * reset ofter a test is complete.
 *
 * This test uses the PER_CLASS lifecycle so that Nested tests can use the BeforeAll and
 * AfterAll annotations (See Junit docs).
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@RestoreSysPropsAfterAllTests
public class RestoreSysPropsAfterAllTestsAndOneTestTest {
	private static String BEFORE_ALL_PROP = "BEFORE_ALL_PROP";
	private static String TEST_PROP = "TEST_PROP";

	private static String NESTED_SHARED_PROP = "NESTED_SHARED_PROP";

	private static String PROP_VAL = "PROP_VAL";

	@BeforeAll
	static void beforeAll() {
		assertFalse(System.getProperties().containsKey(BEFORE_ALL_PROP));	//Never should be set

		System.setProperty(BEFORE_ALL_PROP, PROP_VAL);
	}

	@AfterAll
	static void afterAll() {
		assertEquals(PROP_VAL, System.getProperty(BEFORE_ALL_PROP), "Still set");
	}


	@BeforeEach
	void beforeEach() {
		assertEquals(PROP_VAL, System.getProperty(BEFORE_ALL_PROP), "Still set from BeforeAll");
	}

	@Test  //In order
	void test1_TEST_PROPshouldNotBeSetButBeforeXPropsAreSet() {
		assertEquals(PROP_VAL, System.getProperty(BEFORE_ALL_PROP));

		assertFalse(System.getProperties().containsKey(TEST_PROP));
	}

	@Test  //In order
	@RestoreSysPropsAfterThisTest
	void test2_TEST_PROPisNowSetInsideThisTest() {
		assertEquals(PROP_VAL, System.getProperty(BEFORE_ALL_PROP));

		//Set TEST_PROP - This test is ann w/ RestoreSysPropsAfterThisTest, so it should reset
		System.setProperty(TEST_PROP, PROP_VAL);
		assertEquals(PROP_VAL, System.getProperty(TEST_PROP));
	}

	@Test  //In order
	void test3_TEST_PROPshouldNolongerBeSet() {
		assertFalse(System.getProperties().containsKey(TEST_PROP),
				"The annotation on t2 should have removed this property");
	}

	/*
	These two identical, nested test classes have full lifecycles to JUnit, meaning they
	have all of the extension events (BeforeAll, BeforeEach... etc).  They should
	also inherit the BeforeAll and Before each state of the main class.

	Since the classes are identical, their run order doesn't matter.

	Both Nested classes share the NESTED_SHARED_PROP, which should be null at the
	start of BeforeAll, but then is set and should remain set everywhere within the tests.
	If NESTED_SHARED_PROP is nonNull at the start of BeforeAll, it means that the extension
	broke its basic guarantee of resetting sys props to their state from before the test.
	*/

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class NestedTestClassWithFullJUnitLifecycle1 {
		@BeforeAll
		void beforeAll() {
			assertFalse(System.getProperties().containsKey(NESTED_SHARED_PROP),
					"If Props contains this key, it means the extension broke it promise and " +
							"failed to reset the sys props between tests.");

			//Now set it
			System.setProperty(NESTED_SHARED_PROP, PROP_VAL);
		}

		@AfterAll
		void afterAll() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
		}


		@BeforeEach
		void beforeEach() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
		}

		@AfterEach
		void afterEach() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
		}

		@Test
		void test1() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
			assertEquals(PROP_VAL, System.getProperty(BEFORE_ALL_PROP));
		}
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class NestedTestClassWithFullJUnitLifecycle2 {
		@BeforeAll
		void beforeAll() {
			assertFalse(System.getProperties().containsKey(NESTED_SHARED_PROP),
					"If Props contains this key, it means the extension broke it promise and " +
							"failed to reset the sys props between tests.");

			//Now set it
			System.setProperty(NESTED_SHARED_PROP, PROP_VAL);
		}

		@AfterAll
		void afterAll() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
		}


		@BeforeEach
		void beforeEach() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
		}

		@AfterEach
		void afterEach() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
		}

		@Test
		void test1() {
			assertEquals(PROP_VAL, System.getProperty(NESTED_SHARED_PROP));
			assertEquals(PROP_VAL, System.getProperty(BEFORE_ALL_PROP));

			assertFalse(System.getProperties().containsKey(TEST_PROP));
		}
	}
}
