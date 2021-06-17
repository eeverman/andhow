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

		Properties originalProperties = System.getProperties();

		try {

			//We shouldn't have this property before we start
			assertNull(System.getProperty(BOB));

			//Take the snapshot
			AndHowTestBase.andHowSnapshotBeforeTestClass();

			//Sys props - completely mess them up - reset should reset them...
			System.setProperties(new Properties());  //zap all properties
			System.setProperty(BOB, BOB);


			//Are the sysProps messed up just like we did above?
			assertEquals(BOB, System.getProperty(BOB));
			assertEquals(1, System.getProperties().size());

			//
			//reset
			AndHowTestBase.resetAndHowSnapshotAfterTestClass();


			//Verify we have the exact same set of SysProps after the reset
			assertEquals(originalProperties.size(), System.getProperties().size());
			assertTrue(originalProperties.entrySet().containsAll(System.getProperties().entrySet()));

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