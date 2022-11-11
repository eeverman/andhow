package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;
import org.yarnandtail.andhow.AndHow;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfigFromFileMixedUsage3Test extends ConfigFromFileMixedUsage3Base {

	@Order(1)
	@Test
	public void test1() {
		assertFalse(AndHow.isInitialized(), "Shouldn't be init before 1st test");
		assertEquals("Bob", Conf1.MY_PROP.getValue());
	}

	// This test does NOT pick up the annotations of the test it overrides
	@Order(2)
	@Test
	@Override
	public void test2() {
		assertTrue(AndHow.isInitialized());
		assertEquals("Bob", Conf1.MY_PROP.getValue());
	}

	@Order(4)
	@Test
	public void test4() {

		assertTrue(AndHow.isInitialized(), "Should be init bc the config state should be restore to post-test1");

		assertEquals("Bob", Conf1.MY_PROP.getValue());
	}

}