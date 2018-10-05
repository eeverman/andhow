/*
 */
package org.yarnandtail.andhow.util;

import java.lang.reflect.Method;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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

	/**
	 * Test of classExists method, of class AndHowUtil.
	 */
	@Test
	public void testClassExists() {
		assertThat(true, is(equalTo(AndHowUtil.classExists("java.lang.String"))));

		assertThat(false, is(equalTo(AndHowUtil.classExists(""))));
		assertThat(false, is(equalTo(AndHowUtil.classExists(null))));
		assertThat(false, is(equalTo(AndHowUtil.classExists("testClassName"))));
	}

	/**
	 * Test of getClassForName method, of class AndHowUtil.
	 */
	@Test
	public void testGetClassForName() {
		assertThat(String.class, is(equalTo(AndHowUtil.getClassForName("java.lang.String"))));

		assertThat(null, is(equalTo(AndHowUtil.getClassForName(""))));
		assertThat(null, is(equalTo(AndHowUtil.getClassForName(null))));
		assertThat(null, is(equalTo(AndHowUtil.getClassForName("testClassName"))));
	}

	/**
	 * Test of getClassInstanceForName method, of class AndHowUtil.
	 */
	@Test
	public void testGetClassInstanceForName() throws IllegalAccessException, InstantiationException {
		assertThat(String.class.newInstance(), is(equalTo(AndHowUtil.getClassInstanceForName("java.lang.String"))));

		assertThat(null, is(equalTo(AndHowUtil.getClassInstanceForName(""))));
		assertThat(null, is(equalTo(AndHowUtil.getClassInstanceForName(null))));
		assertThat(null, is(equalTo(AndHowUtil.getClassInstanceForName("testClassName"))));
		assertThat(null, is(equalTo(AndHowUtil.getClassInstanceForName("java.lang.Runnable"))));

	}

}
