package org.yarnandtail.andhow;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class AndHowTestBaseImplTest {

	static final String BOB_NAME = SimpleConfig.class.getCanonicalName() + ".BOB";
	static final String BOB_VALUE = "BobsYourUncle";

	@Test
	void andHowSnapshotBeforeAndAfterTestClass() {

		AndHowCore beforeTheTestCore = AndHowNonProductionUtil.getAndHowCore();
		Properties originalProperties = System.getProperties();
		Level beforeClassLogLevel = Logger.getGlobal().getLevel();  //store log level before class

		try {

			//We shouldn't have this property before we start
			assertNull(System.getProperty(BOB_NAME));

			//Take the snapshot
			AndHowTestBaseImpl.andHowSnapshotBeforeTestClass();

			//Sys props - completely mess them up - reset should reset them...
			System.setProperties(new Properties());	//zap all properties
			System.setProperty(BOB_NAME, BOB_VALUE);

			//Now build w/ the new SystemProperties
			NonProductionConfig.instance().group(SimpleConfig.class).forceBuild();


			//Are the sysProps messed up just like we did above?
			assertEquals(BOB_VALUE, System.getProperty(BOB_NAME));
			assertEquals(1, System.getProperties().size());

			//Did the AndHow Property get set?
			assertEquals(BOB_VALUE, SimpleConfig.BOB.getValue());

			//Change the global log level
			if (beforeClassLogLevel == null || ! beforeClassLogLevel.equals(Level.FINEST)) {
				Logger.getGlobal().setLevel(Level.FINEST);
			} else {
				Logger.getGlobal().setLevel(Level.FINER);
			}

			//Is the AndHowCore different than what it was originally?
			assertFalse(beforeTheTestCore == AndHowNonProductionUtil.getAndHowCore());

			//
			//reset
			AndHowTestBaseImpl.resetAndHowSnapshotAfterTestClass();


			//Verify we have the exact same set of SysProps after the reset
			assertEquals(originalProperties.size(), System.getProperties().size());
			assertTrue(originalProperties.entrySet().containsAll(System.getProperties().entrySet()));

			//Verify the core is back to the original
			assertTrue(beforeTheTestCore == AndHowNonProductionUtil.getAndHowCore());

			//Did the log level get reset?
			assertEquals(beforeClassLogLevel, Logger.getGlobal().getLevel());

		} finally {
			AndHowNonProductionUtil.setAndHowCore(beforeTheTestCore);
			System.setProperties(originalProperties);
		}

	}


	@Test
	void andHowSnapshotBeforeAndAfterSingleTest() {

		AndHowTestBaseImpl testBase = new AndHowTestBaseImpl();

		AndHowCore beforeTheTestCore = AndHowNonProductionUtil.getAndHowCore();
		Properties originalProperties = System.getProperties();

		try {

			//We shouldn't have this property before we start
			assertNull(System.getProperty(BOB_NAME));

			//Take the snapshot
			testBase.andHowSnapshotBeforeSingleTest();

			//Sys props - completely mess them up - reset should reset them...
			System.setProperties(new Properties());	//zap all properties
			System.setProperty(BOB_NAME, BOB_VALUE);

			//Now build w/ the new SystemProperties
			NonProductionConfig.instance().group(SimpleConfig.class).forceBuild();


			//Are the sysProps messed up just like we did above?
			assertEquals(BOB_VALUE, System.getProperty(BOB_NAME));
			assertEquals(1, System.getProperties().size());

			//Did the AndHow Property get set?
			assertEquals(BOB_VALUE, SimpleConfig.BOB.getValue());

			//Is the AndHowCore different than what it was originally?
			assertFalse(beforeTheTestCore == AndHowNonProductionUtil.getAndHowCore());

			//
			//reset
			testBase.resetAndHowSnapshotAfterSingleTest();


			//Verify we have the exact same set of SysProps after the reset
			assertEquals(originalProperties.size(), System.getProperties().size());
			assertTrue(originalProperties.entrySet().containsAll(System.getProperties().entrySet()));

			//Verify the core is back to the original
			assertTrue(beforeTheTestCore == AndHowNonProductionUtil.getAndHowCore());

		} finally {
			AndHowNonProductionUtil.setAndHowCore(beforeTheTestCore);
			System.setProperties(originalProperties);
		}

	}


}