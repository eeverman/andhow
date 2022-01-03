/*
 */
package org.yarnandtail.andhow;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.internal.AndHowCore;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class AndHowNonProductionUtilTest extends AndHowTestingTestBase {

	public AndHowNonProductionUtilTest() {
	}

	/**
	 * Test of getAndHowInstance method, of class AndHowNonProductionUtil.
	 */
	@Test
	public void testGetAndHowInstance() {

		//During testing using the AndHowTestingTestBase class, there will not
		//be an instance unless explicitly created during a test.
		assertNull(AndHowNonProductionUtil.getAndHowInstance());
		assertNull(AndHowNonProductionUtil.getAndHowCore());

		AndHow normalAhInstance = AndHow.instance();	//force creation

		AndHow ahInst = AndHowNonProductionUtil.getAndHowInstance();
		AndHowCore ahCore = AndHowNonProductionUtil.getAndHowCore();

		assertNotNull(ahInst);
		assertTrue(normalAhInstance == ahInst);
		assertNotNull(ahCore);
	}


	/**
	 * Test of setAndHowCore method, of class AndHowNonProductionUtil.
	 */
	@Test
	public void testSetAndHowCore() {
		assertNull(AndHowNonProductionUtil.getAndHowInstance());	//just checking

		AndHow.instance();	//force creation

		AndHowCore ahCore1 = AndHowNonProductionUtil.getAndHowCore();

		AndHowTestingTestBase.destroyAndHow();

		assertNull(AndHowNonProductionUtil.getAndHowInstance());	//just checking

		AndHow.instance();	//force creation

		assertFalse(ahCore1 == AndHowNonProductionUtil.getAndHowCore());


		AndHowNonProductionUtil.setAndHowCore(ahCore1);	//swap cores

		//The key assertion
		assertTrue(ahCore1 == AndHowNonProductionUtil.getAndHowCore(), "Should have inserted a new core");
	}

	/**
	 * Test of forceRebuild method, of class AndHowNonProductionUtil.
	 */
	@Test
	public void testForceRebuild() {
		assertNull(AndHowNonProductionUtil.getAndHowInstance());	//just checking

		AndHowConfiguration config = AndHow.findConfig();
		AndHowNonProductionUtil.forceRebuild(config);	//Should be OK even when a new build
		AndHowCore ahCore1 = AndHowNonProductionUtil.getAndHowCore();
		assertNotNull(ahCore1, "Util should create a new instance even if no current instance");

		AndHowNonProductionUtil.forceRebuild(config);	//Now an actual rebuild
		AndHowCore ahCore2 = AndHowNonProductionUtil.getAndHowCore();
		assertNotNull(ahCore2);
		assertFalse(ahCore1 == ahCore2, "The core instances should be different instances");
	}

	/**
	 * Test of clone method, of class AndHowNonProductionUtil.
	 */
	@Test
	public void testClone() {
		Properties props1 = new Properties();
		props1.setProperty("ONE", "one");
		props1.setProperty("TWO", "two");

		Properties props2 = (Properties) props1.clone();

		assertTrue(props1 != props2);
		assertEquals(2, props2.size());
		assertEquals(props2.getProperty("ONE"), "one");
		assertEquals(props2.getProperty("TWO"), "two");
	}

	/**
	 * Test of destroyAndHowCore method, of class AndHowNonProductionUtil.
	 */
	@Test
	public void testDestroyAndHowCore() {

		assertNull(AndHowNonProductionUtil.getAndHowInstance(), "AndHow should be null at test start");
		AndHow.instance();	//force creation
		AndHowNonProductionUtil.destroyAndHowCore();
		assertNotNull(AndHowNonProductionUtil.getAndHowInstance());
		assertNull(AndHowNonProductionUtil.getAndHowCore());
	}

}
