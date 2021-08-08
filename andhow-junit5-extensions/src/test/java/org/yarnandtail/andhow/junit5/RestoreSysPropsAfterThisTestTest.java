package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test order is important here because we want to verify that Sys Props are
 * reset ofter a test is complete.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class RestoreSysPropsAfterThisTestTest {
	private static String TEST_PROP = "TEST_PROP";

	private static String PROP_VAL = "PROP_VAL";

	@Test  //In order
	void test1_TEST_PROPshouldNotBeSetYet() {
		assertFalse(System.getProperties().containsKey(TEST_PROP));
	}

	@Test  //In order
	@RestoreSysPropsAfterThisTest
	void test2_TEST_PROPisNowSetInsideThisTest() {
		System.setProperty(TEST_PROP, PROP_VAL);
		assertEquals(PROP_VAL, System.getProperty(TEST_PROP));
	}

	@Test  //In order
	void test3_TEST_PROPshouldNolongerBeSet() {
		assertFalse(System.getProperties().containsKey(TEST_PROP));
	}
}
