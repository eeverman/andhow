/*
 */
package org.yarnandtail.andhow.util;

import java.lang.reflect.Method;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class AndHowUtilTest {
	

	/**
	 * Test of findMethod method, of class AndHowUtil.
	 */
	@Test
	public void testFindMethod() throws Exception {
		C c = new C();
		
		Method m = AndHowUtil.findMethod(c.getClass(), "one");
		
		assertEquals(1, (int) m.invoke(c, null));
		
		m = AndHowUtil.findMethod(c.getClass(), "twoTimes", Integer.TYPE);
		
		assertEquals(6, (int) m.invoke(c, new Integer(3)));
	}
	
	
	//
	// Class set for testing findMethod
	public static class A {
		private int one() { return 1; }
		private int twoTimes(int base) { return base * 2; }
	}
	
	public static class B extends A {

	}
	
	public static class C extends B {

	}
	
}
