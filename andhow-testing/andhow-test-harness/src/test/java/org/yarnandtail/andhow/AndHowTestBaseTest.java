package org.yarnandtail.andhow;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The implementation is all in the base class, so here tests are minimal
 * to make sure the base class is being called.
 */
class AndHowTestBaseTest {

	static final String BOB = "bob";

	@Test
	void andHowSnapshotBeforeAndAfterTestClass() {

		Properties originalProperties = (Properties) System.getProperties().clone();

		try {

			//We shouldn't have this property before we start
			assertFalse(System.getProperties().containsKey(BOB));

			//Take the snapshot
			AndHowTestBase.andHowSnapshotBeforeTestClass();

			System.setProperty(BOB, BOB);

			assertEquals(BOB, System.getProperty(BOB));

			//
			//reset
			AndHowTestBase.resetAndHowSnapshotAfterTestClass();

			assertFalse(System.getProperties().containsKey(BOB));

		} finally {
			System.setProperties(originalProperties);
		}

	}


	@Test
	void andHowSnapshotBeforeAndAfterSingleTest() {

		AndHowTestBase testBase = new AndHowTestBase();
		AndHowTestBaseImplTest implTest = new AndHowTestBaseImplTest();

		implTest.doAndHowSnapshotBeforeAndAfterSingleTest(testBase);

	}


}