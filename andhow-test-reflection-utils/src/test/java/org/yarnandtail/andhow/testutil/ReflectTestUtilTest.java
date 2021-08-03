package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReflectTestUtilTest {

	//
	// Sample private instance and static fields to test against

	private String myString = "Carl";
	private int myInt = 42;

	private static String myStaticString = "Static";
	private static int myStaticInt = 43;

	//
	//Sample private String methods to try to invoke

	private String sampleMethod1(String name, int aNumber) {
		return name + aNumber;
	}

	private String sampleMethod2(boolean flag) {
		return "BOB" + flag;
	}

	private String sampleMethod3(String name, Integer aNumber) {
		return name + aNumber;
	}

	private String sampleMethod4(Boolean flag) {
		return "BOB" + flag;
	}

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void stringMethodWithArrayOfParamsAndTypes() {
		assertEquals(
				"MyName4",
				ReflectTestUtil.stringMethod(this, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);
	}

	@Test
	void testStringMethodWithSingleParamAndType() {
		assertEquals(
				"BOBtrue",
				ReflectTestUtil.stringMethod(this, "sampleMethod2",
						true, boolean.class)
		);
	}

	@Test
	void testStringMethodWithSingleArrayOfArguments() {
		assertEquals(
				"MyName5",
				ReflectTestUtil.stringMethod(this, "sampleMethod3","MyName", 5)
		);
		assertEquals(
				"BOBtrue",
				ReflectTestUtil.stringMethod(this, "sampleMethod4",true)
		);
	}

	@Test
	void invokeMethod() {
		assertEquals(
				"MyName4",
				ReflectTestUtil.invokeMethod(this, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);
	}


	@Test
	void getInstanceFieldValue() {
	}

	@Test
	void setInstanceFieldValue() {
	}

	@Test
	void getStaticFieldValue() {
	}

	@Test
	void setStaticFieldValue() {
	}

	@Test
	void getWritableField() {
	}

	@Test
	void getField() {
	}


	@Test
	void getTypes() {
	}
}