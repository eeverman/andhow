/*
 */
package org.yarnandtail.andhow.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

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

	/**
	 * Test of classExists method, of class AndHowUtil.
	 */
	@Test
	public void testClassExists() {
		assertTrue(AndHowUtil.classExists("java.lang.String"));

		assertFalse(AndHowUtil.classExists(""));
		assertFalse(AndHowUtil.classExists(null));
		assertFalse(AndHowUtil.classExists("testClassName"));
	}

	/**
	 * Test of getClassForName method, of class AndHowUtil.
	 */
	@Test
	public void testGetClassForName() {
		assertEquals(String.class, AndHowUtil.getClassForName("java.lang.String"));

		assertEquals(null, AndHowUtil.getClassForName(""));
		assertEquals(null, AndHowUtil.getClassForName(null));
		assertEquals(null, AndHowUtil.getClassForName("testClassName"));
	}

	/**
	 * Test of getClassInstanceForName method, of class AndHowUtil.
	 */
	@Test
	public void testGetClassInstanceForName() throws IllegalAccessException, InstantiationException {
		assertEquals(String.class.newInstance(), AndHowUtil.getClassInstanceForName("java.lang.String"));

		assertEquals(null, AndHowUtil.getClassInstanceForName(""));
		assertEquals(null, AndHowUtil.getClassInstanceForName(null));
		assertEquals(null, AndHowUtil.getClassInstanceForName("testClassName"));

	}

}
