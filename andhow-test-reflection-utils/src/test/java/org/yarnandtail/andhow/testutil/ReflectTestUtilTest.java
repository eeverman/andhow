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
	private Integer myInteger = 43;

	private static String myStaticString = "Static";
	private static int myStaticInt = 542;
	private static Integer myStaticInteger = 543;

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
		myString = "Carl";
		myInt = 42;
		myInteger = 43;
		myStaticString = "Static";
		myStaticInt = 542;
		myStaticInteger = 543;
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
		assertEquals(
				"Carl",
				ReflectTestUtil.getInstanceFieldValue(this, "myString", String.class)
		);
		assertEquals(
				42,
				ReflectTestUtil.getInstanceFieldValue(this, "myInt", int.class)
		);
		assertEquals(
				43,
				ReflectTestUtil.getInstanceFieldValue(this, "myInteger", Integer.class)
		);
	}

	@Test
	void setInstanceFieldValue() {
		assertEquals(
				"Carl",
				ReflectTestUtil.setInstanceFieldValue(this, "myString", "Bob", String.class),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectTestUtil.setInstanceFieldValue(this, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectTestUtil.setInstanceFieldValue(this, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectTestUtil.getInstanceFieldValue(this, "myString", String.class)
		);
		assertNull(
				ReflectTestUtil.setInstanceFieldValue(this, "myString", "NotNull", String.class),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				42,
				ReflectTestUtil.setInstanceFieldValue(this, "myInt", Integer.valueOf(99), int.class),
				"Should return the previous value"
		);
		assertEquals(
				99,
				ReflectTestUtil.setInstanceFieldValue(this, "myInt", 100, int.class),
				"Should return the previous value"
		);
		assertThrows(IllegalArgumentException.class,
				() -> ReflectTestUtil.setInstanceFieldValue(this, "myInt", null, int.class),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				43,
				ReflectTestUtil.setInstanceFieldValue(this, "myInteger", Integer.valueOf(999), Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectTestUtil.setInstanceFieldValue(this, "myInteger", 1000, Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				1000,
				ReflectTestUtil.setInstanceFieldValue(this, "myInteger", null, Integer.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectTestUtil.getInstanceFieldValue(this, "myInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectTestUtil.setInstanceFieldValue(this, "myInteger", 1001, Integer.class)
		);

	}

	@Test
	void getStaticFieldValue() {
		assertEquals(
				"Static",
				ReflectTestUtil.getStaticFieldValue(this.getClass(), "myStaticString", String.class)
		);
		assertEquals(
				542,
				ReflectTestUtil.getStaticFieldValue(this.getClass(), "myStaticInt", int.class)
		);
		assertEquals(
				543,
				ReflectTestUtil.getStaticFieldValue(this.getClass(), "myStaticInteger", Integer.class)
		);
	}

	@Test
	void setStaticFieldValue() {
		assertEquals(
				"Static",
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticString", "Bob"),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectTestUtil.getStaticFieldValue(this.getClass(), "myStaticString", String.class)
		);
		assertNull(
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticString", "NotNull"),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				542,
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticInt", Integer.valueOf(999)),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticInt", 1000),
				"Should return the previous value"
		);
		assertThrows(IllegalArgumentException.class,
				() -> ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticInt", null),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				543,
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticInteger", Integer.valueOf(9999)),
				"Should return the previous value"
		);
		assertEquals(
				9999,
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticInteger", 10000),
				"Should return the previous value"
		);
		assertEquals(
				10000,
				(Integer) ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticInteger", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectTestUtil.getStaticFieldValue(this.getClass(), "myStaticInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectTestUtil.setStaticFieldValue(this.getClass(), "myStaticInteger", 10001)
		);

	}


	//
	// These are all implicitly tested within the other methods
//	@Test
//	void getWritableField() {
//	}
//
//	@Test
//	void getField() {
//	}
//
//
//	@Test
//	void getTypes() {
//	}
}