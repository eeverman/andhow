package org.yarnandtail.andhow;

import java.util.Properties;

import org.junit.jupiter.api.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

/**
 * A test base class that COMPLETELY kills AndHow between each test and test classes.
 * <p>
 * This util class is intentionally placed in the test directory because it is
 * never intended to be distributed, not even for use by others in their testing.
 * <p>
 * <em>THIS CLASS DOES DANGEROUS THINGS THAT SHOULD NEVER BE DONE NEAR APPLICATION CODE</em>
 * <p>
 * For utilities to test your application code with AndHow, see the AndHow
 * Test Harness module.  For examples using the test harness, see the AndHow
 * Sample Usages module and its many sub-modules, which are stand-alone simulated
 * projects complete with testing.
 *
 */
public class AndHowTestBase {
	
	/**
	 * System properties before the tests and subclass @BeforeClass initializer
	 * are run.  Properties prior to the initialization of this test class are
	 * stored and then reinstated after the test class is complete.
	 */
	private static Properties beforeClassSystemProps;
	
	/**
	 * System properties before an individual test is run and the subclass @Before
	 * initializers are run.  Properties prior to a test are
	 * stored and then reinstated after the test is complete.  If a Test classes
	 * uses a @BeforeClass initializer that sets system properties, this will
	 * reinstate to that state.
	 */
	private Properties beforeTestSystemProps;

	
	@BeforeAll
	public static void killAndHowStateBeforeClass() {
		AndHowTestUtils.killAndHowFully();
	}
	
	@BeforeAll
	public static void storeSysPropsBeforeClass() {
		beforeClassSystemProps = (Properties)System.getProperties().clone();
	}
	
	@BeforeEach
	public void killAndHowStateBeforeTest() {
		AndHowTestUtils.killAndHowFully();
	}
	
	@BeforeEach
	public void storeSysPropsBeforeTest() {
		beforeTestSystemProps = (Properties)System.getProperties().clone();
	}
	
	@AfterEach
	public void killAndHowStateAfterTest() {
		AndHowTestUtils.killAndHowFully();
	}
	
	@AfterEach
	public void restoreSysPropsAfterTest() {
		System.setProperties(beforeTestSystemProps);
	}
	
	
	@AfterAll
	public static void killAndHowStateAfterClass() {
		AndHowTestUtils.killAndHowFully();
	}
	
	@AfterAll
	public static void restoreSysPropsAfterClass() {
		System.setProperties(beforeClassSystemProps);
	}
	
}
