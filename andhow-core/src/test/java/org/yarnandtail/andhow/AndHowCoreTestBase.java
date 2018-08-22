package org.yarnandtail.andhow;

import org.junit.*;

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
 * 
 * @author ericeverman
 */
public class AndHowCoreTestBase {
	
	@BeforeClass
	public static void killAndHowStateBeforeClass() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@Before
	public void killAndHowStateBeforeTest() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@After
	public void killAndHowStateAfterTest() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@AfterClass
	public static void killAndHowStateAfterClass() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
}
