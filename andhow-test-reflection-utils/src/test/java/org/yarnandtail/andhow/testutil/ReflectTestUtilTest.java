package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

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

	// subcclass of this class that masks some values
	static class Subclass extends ReflectTestUtilTest {
		private String myString = "SubCarl";
		private static String myStaticString = "SubStatic";
	}

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

		//Subclass
		Subclass.myStaticString = "SubStatic";
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void stringMethodWithArrayOfParamsAndTypes() {
		assertEquals(
				"MyName4",
				ReflectionUtils.stringMethod(this, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);
	}

	@Test
	void testStringMethodWithSingleParamAndType() {
		assertEquals(
				"BOBtrue",
				ReflectionUtils.stringMethod(this, "sampleMethod2",
						true, boolean.class)
		);
	}

	@Test
	void testStringMethodWithSingleArrayOfArguments() {
		assertEquals(
				"MyName5",
				ReflectionUtils.stringMethod(this, "sampleMethod3","MyName", 5)
		);
		assertEquals(
				"BOBtrue",
				ReflectionUtils.stringMethod(this, "sampleMethod4",true)
		);
	}

	@Test
	void invokeMethod() {
		assertEquals(
				"MyName4",
				ReflectionUtils.invokeMethod(this, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> ReflectionUtils.invokeMethod(this, "IDONTEXIST",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);

	}


	@Test
	void getInstanceFieldValue() {

		// Directly on class
		assertEquals(
				"Carl",
				ReflectionUtils.getInstanceFieldValue(this, "myString", String.class)
		);
		assertEquals(
				42,
				ReflectionUtils.getInstanceFieldValue(this, "myInt", int.class)
		);
		assertEquals(
				43,
				ReflectionUtils.getInstanceFieldValue(this, "myInteger", Integer.class)
		);

		//On the subclass
		Subclass sub = new Subclass();
		assertEquals(
				"SubCarl",
				ReflectionUtils.getInstanceFieldValue(sub, "myString", String.class)
		);
	}

	@Test
	void setInstanceFieldValue() {
		assertEquals(
				"Carl",
				ReflectionUtils.setInstanceFieldValue(this, "myString", "Bob", String.class),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionUtils.setInstanceFieldValue(this, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.setInstanceFieldValue(this, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.getInstanceFieldValue(this, "myString", String.class)
		);
		assertNull(
				ReflectionUtils.setInstanceFieldValue(this, "myString", "NotNull", String.class),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				42,
				ReflectionUtils.setInstanceFieldValue(this, "myInt", Integer.valueOf(99), int.class),
				"Should return the previous value"
		);
		assertEquals(
				99,
				ReflectionUtils.setInstanceFieldValue(this, "myInt", 100, int.class),
				"Should return the previous value"
		);
		assertThrows(RuntimeException.class,
				() -> ReflectionUtils.setInstanceFieldValue(this, "myInt", null, int.class),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				43,
				ReflectionUtils.setInstanceFieldValue(this, "myInteger", Integer.valueOf(999), Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectionUtils.setInstanceFieldValue(this, "myInteger", 1000, Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				1000,
				ReflectionUtils.setInstanceFieldValue(this, "myInteger", null, Integer.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.getInstanceFieldValue(this, "myInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectionUtils.setInstanceFieldValue(this, "myInteger", 1001, Integer.class)
		);

		//
		// on the subclass
		Subclass sub = new Subclass();
		assertEquals(
				"SubCarl",
				ReflectionUtils.setInstanceFieldValue(sub, "myString", "Bob", String.class),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionUtils.setInstanceFieldValue(sub, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.setInstanceFieldValue(sub, "myString", null, String.class),
				"Should return the previous value"
		);
	}

	@Test
	void getStaticFieldValue() {
		assertEquals(
				"Static",
				ReflectionUtils.getStaticFieldValue(this.getClass(), "myStaticString", String.class)
		);
		assertEquals(
				542,
				ReflectionUtils.getStaticFieldValue(this.getClass(), "myStaticInt", int.class)
		);
		assertEquals(
				543,
				ReflectionUtils.getStaticFieldValue(this.getClass(), "myStaticInteger", Integer.class)
		);

		//
		// on the subclass
		assertEquals(
				"SubStatic",
				ReflectionUtils.getStaticFieldValue(Subclass.class, "myStaticString", String.class)
		);
	}

	@Test
	void setStaticFieldValue() {
		assertEquals(
				"Static",
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticString", "Bob"),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.getStaticFieldValue(this.getClass(), "myStaticString", String.class)
		);
		assertNull(
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticString", "NotNull"),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				542,
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticInt", Integer.valueOf(999)),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticInt", 1000),
				"Should return the previous value"
		);
		assertThrows(RuntimeException.class,
				() -> ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticInt", null),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				543,
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", Integer.valueOf(9999)),
				"Should return the previous value"
		);
		assertEquals(
				9999,
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", 10000),
				"Should return the previous value"
		);
		assertEquals(
				10000,
				(Integer) ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.getStaticFieldValue(this.getClass(), "myStaticInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectionUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", 10001)
		);

		//
		// on the subclass
		assertEquals(
				"SubStatic",
				ReflectionUtils.setStaticFieldValue(Subclass.class, "myStaticString", "Bob"),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionUtils.setStaticFieldValue(Subclass.class, "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionUtils.setStaticFieldValue(Subclass.class, "myStaticString", null),
				"Should return the previous value"
		);
	}

	@Test
	void getWritableField() {
		//Most test cases are already implicit in the other tests

		assertThrows(IllegalArgumentException.class,
				() -> ReflectionUtils.getWritableField(this.getClass(), "IDONOTEXIST")
		);

	}

	@Test
	void getTypes() {
		//Most test cases are already implicit in the other tests

		assertEquals(0, ReflectionUtils.getTypes().length);
		assertEquals(0, ReflectionUtils.getTypes(null).length);

		assertThrows(RuntimeException.class,
				() -> ReflectionUtils.getTypes("", null)
		);
	}

	@Test
	void getFieldValueTest() {
		Field msField = ReflectionUtils.getWritableField(this.getClass(), "myString");
		assertEquals("Carl", ReflectionUtils.getFieldValue(msField, this));

		try {
			msField.setAccessible(false);
			assertThrows(
					RuntimeException.class,
					() -> ReflectionUtils.getFieldValue(msField, this));
		} finally {
			msField.setAccessible(true);
		}
	}

	@Test
	void setFieldValueTest() {
		Field msField = ReflectionUtils.getWritableField(this.getClass(), "myString");
		assertEquals("Carl", ReflectionUtils.setFieldValue(msField, this, "Bob"));

		try {
			msField.setAccessible(false);
			assertThrows(
					RuntimeException.class,
					() -> ReflectionUtils.setFieldValue(msField, this, "wont work"));
		} finally {
			msField.setAccessible(true);
		}
	}
}