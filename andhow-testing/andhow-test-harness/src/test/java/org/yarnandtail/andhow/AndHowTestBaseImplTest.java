package org.yarnandtail.andhow;

import org.junit.jupiter.api.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.internal.AndHowCore;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class AndHowTestBaseImplTest {

	static final String BOB_NAME = SimpleConfig.class.getCanonicalName() + ".BOB";
	static final String BOB_VALUE = "BobsYourUncle";

	@Test
	void andHowSnapshotBeforeAndAfterTestClass() throws NamingException {

		AndHowTestBaseImpl testBase = new AndHowTestBaseImpl();

		AndHowCore beforeTheTestCore = AndHowNonProductionUtil.getAndHowCore();
		Properties originalProperties = System.getProperties();
		Level beforeClassLogLevel = Logger.getGlobal().getLevel();  //store log level before class

		try {

			//We shouldn't have this property before we start
			assertNull(System.getProperty(BOB_NAME));

			//Take the snapshot
			AndHowTestBaseImpl.andHowSnapshotBeforeTestClass();

			//Sys props - completely mess them up - reset should reset them...
			System.setProperties(new Properties());  //zap all properties
			System.setProperty(BOB_NAME, BOB_VALUE);

			//Now build w/ the new SystemProperties
			NonProductionConfig.instance().group(SimpleConfig.class).forceBuild();


			//Are the sysProps messed up just like we did above?
			assertEquals(BOB_VALUE, System.getProperty(BOB_NAME));
			assertEquals(1, System.getProperties().size());

			//Did the AndHow Property get set?
			assertEquals(BOB_VALUE, SimpleConfig.BOB.getValue());

			//Change the global log level
			if (beforeClassLogLevel == null || !beforeClassLogLevel.equals(Level.FINEST)) {
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

	/**
	 * Simulating the progression of a test where jndi properties are
	 * set at the start of a single test.
	 * @throws NamingException
	 */
	@Test
	public void getJndiTest() throws NamingException {

		AndHowTestBaseImpl testBase = new AndHowTestBaseImpl();


		try {

			//Start of test class and before a test
			AndHowTestBaseImpl.andHowSnapshotBeforeTestClass();
			testBase.andHowSnapshotBeforeSingleTest();

			//Now we are setting up for a single test w/ a JNDI property
			SimpleNamingContextBuilder jndi = testBase.getJndi();
			jndi.bind("java:" + BOB_NAME, BOB_VALUE + "_JNDI");
			jndi.activate();

			//Can we read the JNDI property?
			final InitialContext ctx = new InitialContext();  // Jndi Context should have set value
			assertEquals(BOB_VALUE + "_JNDI", ctx.lookup("java:" + BOB_NAME));

			//
			//Now build AndHow - should see JNDI property
			NonProductionConfig.instance().group(SimpleConfig.class).forceBuild();

			//Did the AndHow Property get set?
			assertEquals(BOB_VALUE + "_JNDI", SimpleConfig.BOB.getValue());

			//End of one test and the start of another
			testBase.resetAndHowSnapshotAfterSingleTest();
			testBase.andHowSnapshotBeforeSingleTest();

			assertThrows(NamingException.class, () ->
							ctx.lookup("java:" + BOB_NAME)
					);

			//
			//Now build AndHow again - 'BOB' should be empty- should see JNDI property
			NonProductionConfig.instance().group(SimpleConfig.class).forceBuild();

			assertNull(SimpleConfig.BOB.getValue());

			//End of one test and the start of another
			testBase.resetAndHowSnapshotAfterSingleTest();
			testBase.andHowSnapshotBeforeSingleTest();

			//Activate JNDI again - it should still be empty
			testBase.getJndi().activate();

			//should still be empty on the original context
			assertThrows(NamingException.class, () ->
					ctx.lookup("java:" + BOB_NAME)
			);

			//... and on a new context
			final InitialContext ctx2 = new InitialContext();
			assertThrows(NamingException.class, () ->
					ctx2.lookup("java:" + BOB_NAME)
			);

		} finally {
			testBase.resetAndHowSnapshotAfterSingleTest();
			AndHowTestBaseImpl.resetAndHowSnapshotAfterTestClass();
		}
	}

}