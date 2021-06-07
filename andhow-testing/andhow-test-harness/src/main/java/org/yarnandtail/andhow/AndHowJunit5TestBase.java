package org.yarnandtail.andhow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;


/**
 * Optional class that can be used by app developers as a base class for JUnit 4 tests.
 *
 * This class resets AndHow and System.properties to their prior
 * state after each test run, as well as after all tests have run.  This, used with
 * <code>NonProductionConfig.instance().forceBuild()</code>
 * and setting SystemProperties at the start of a test, can be used to set a
 * specific AndHow configuration for a test or suite of tests.
 *
 * <a href="https://github.com/eeverman/andhow/blob/master/andhow-testing/andhow-simulated-app-tests/andhow-multimodule-dataprocess/andhow-default-behavior-dep1/src/test/java/com/dep1/EarthMapMakerUsingAHBaseTestClassTest.java#L25">
 *   Here is a example</a>Here is a example that shows how this can be used.
 *
 * @author eeverman
 */
public class AndHowJunit5TestBase extends AndHowTestBaseImpl {

	/**
	 * Stores the AndHow Core (its state) and System Properties prior to a test class.
	 * It also sets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to SEVERE.  If JNDI is used for a test, it's startup
	 * is verbose to System.out, so this turns it off.
	 */
	@BeforeAll
	public static void andHowSnapshotBeforeTestClass() {
		AndHowTestBaseImpl.andHowSnapshotBeforeTestClass();
	}

	/**
	 * Restores the AndHow Core (its state) and System Properties
	 * that were previously stored prior to this class' test run.
	 * It also resets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to what ever it was prior to the run.
	 */
	@AfterAll
	public static void resetAndHowSnapshotAfterTestClass() {
		AndHowTestBaseImpl.resetAndHowSnapshotAfterTestClass();
	}

	/**
	 * Stores the AndHow Core (its state) and System Properties prior to a test.
	 * It also sets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to SEVERE.  If JNDI is used for a test, it's startup
	 * is verbose to System.out, so this turns it off.
	 */
	@BeforeEach
	public void andHowSnapshotBeforeSingleTest() {
		super.andHowSnapshotBeforeSingleTest();
	}

	/**
	 * Restores the AndHow Core (its state) and System Properties
	 * that were previously stored prior to a test run.
	 * It also resets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to what ever it was prior to the run.
	 */
	@AfterEach
	public void resetAndHowSnapshotAfterSingleTest() {
		super.resetAndHowSnapshotAfterSingleTest();
	}

}
