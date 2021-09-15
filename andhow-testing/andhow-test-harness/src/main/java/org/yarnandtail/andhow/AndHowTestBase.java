package org.yarnandtail.andhow;

import org.junit.*;


/**
 * Optional class that can be used by app developers as a base class for JUnit 4 tests.
 *
 * This class resets AndHow and System.properties to their prior
 * state after each test run, as well as after all tests have run.  This, used with
 * <code>NonProductionConfig.instance().forceBuild(MyNew_AndHowConfiguration)</code>
 * and setting SystemProperties at the start of a test, can be used to set a
 * specific AndHow configuration for a test or suite of tests.
 *
 * Here is a example that shows how this can be used (Note the link goes to version 1.4.1,
 * since the use of this base class is no longer the preferred way to do testing) :
 * <a href="https://github.com/eeverman/andhow/blob/andhow-0.4.1/andhow-testing/andhow-simulated-app-tests/andhow-multimodule-dataprocess/andhow-default-behavior-dep1/src/test/java/com/dep1/EarthMapMakerUsingAHBaseTestClassTest.java#L25">
 *
 * @deprecated Switch to JUnit5 and the annotations in the {@link org.yarnandtail.andhow.junit5}
 * package, which is better, safer and easier to use.
 */
@Deprecated
public class AndHowTestBase extends AndHowTestBaseImpl {

	/**
	 * Stores the AndHow Core (its state) and System Properties prior to a test class.
	 * It also sets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to SEVERE.  If JNDI is used for a test, it's startup
	 * is verbose to System.out, so this turns it off.
	 */
	@BeforeClass
	public static void andHowSnapshotBeforeTestClass() {
		AndHowTestBaseImpl.andHowSnapshotBeforeTestClass();
	}

	/**
	 * Restores the AndHow Core (its state) and System Properties
	 * that were previously stored prior to this class' test run.
	 * It also resets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to what ever it was prior to the run.
	 */
	@AfterClass
	public static void resetAndHowSnapshotAfterTestClass() {
		AndHowTestBaseImpl.resetAndHowSnapshotAfterTestClass();
	}

	/**
	 * Stores the AndHow Core (its state) and System Properties prior to a test.
	 * It also sets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to SEVERE.  If JNDI is used for a test, it's startup
	 * is verbose to System.out, so this turns it off.
	 */
	@Before
	public void andHowSnapshotBeforeSingleTest() {
		super.andHowSnapshotBeforeSingleTest();
	}

	/**
	 * Restores the AndHow Core (its state) and System Properties
	 * that were previously stored prior to a test run.
	 * It also resets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to what ever it was prior to the run.
	 */
	@After
	public void resetAndHowSnapshotAfterSingleTest() {
		super.resetAndHowSnapshotAfterSingleTest();
	}

}
