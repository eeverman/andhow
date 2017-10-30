package org.yarnandtail.andhow.util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is testing production code from Log4J, so this is really just characterization.
 * @author ericeverman
 */
public class StackLocatorTest {

	/**
	 * Test of getCallerClass method, of class StackLocator.
	 */
	@Test
	public void testGetCallerClass_int() {
		assertEquals("org.yarnandtail.andhow.util.StackLocatorTest", StackLocator.getInstance().getCallerClass(1).getCanonicalName());
	}


	/**
	 * Test of calcLocation method, of class StackLocator.
	 */
	@Test
	public void testCalcLocation() {
		TestCalcLocation.callMe();
	}
	
	public static class TestCalcLocation {
		static void callMe() {
			StackTraceElement ste = StackLocator.getInstance().calcLocation("org.yarnandtail.andhow.util.StackLocatorTest$TestCalcLocation");
			assertEquals("org.yarnandtail.andhow.util.StackLocatorTest", ste.getClassName());
			assertEquals("testCalcLocation", ste.getMethodName());
		}
	}
	
}
