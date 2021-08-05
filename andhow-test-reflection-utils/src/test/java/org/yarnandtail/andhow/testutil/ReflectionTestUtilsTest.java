package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionTestUtilsTest {

	//
	// Sample private instance and static fields to test against

	private String myString = "Carl";
	private int myInt = 42;
	private Integer myInteger = 43;

	private static String myStaticString = "Static";
	private static int myStaticInt = 542;
	private static Integer myStaticInteger = 543;

	// subcclass of this class that masks some values
	static class Subclass extends ReflectionTestUtilsTest {
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
				ReflectionTestUtils.stringMethod(this, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);
	}

	@Test
	void testStringMethodWithSingleParamAndType() {
		assertEquals(
				"BOBtrue",
				ReflectionTestUtils.stringMethod(this, "sampleMethod2",
						true, boolean.class)
		);
	}

	@Test
	void testStringMethodWithSingleArrayOfArguments() {
		assertEquals(
				"MyName5",
				ReflectionTestUtils.stringMethod(this, "sampleMethod3","MyName", 5)
		);
		assertEquals(
				"BOBtrue",
				ReflectionTestUtils.stringMethod(this, "sampleMethod4",true)
		);
	}

	@Test
	void invokeMethod() {
		assertEquals(
				"MyName4",
				ReflectionTestUtils.invokeMethod(this, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> ReflectionTestUtils.invokeMethod(this, "IDONTEXIST",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);

	}


	@Test
	void getInstanceFieldValue() {

		// Directly on class
		assertEquals(
				"Carl",
				ReflectionTestUtils.getInstanceFieldValue(this, "myString", String.class)
		);
		assertEquals(
				42,
				ReflectionTestUtils.getInstanceFieldValue(this, "myInt", int.class)
		);
		assertEquals(
				43,
				ReflectionTestUtils.getInstanceFieldValue(this, "myInteger", Integer.class)
		);

		//On the subclass
		Subclass sub = new Subclass();
		assertEquals(
				"SubCarl",
				ReflectionTestUtils.getInstanceFieldValue(sub, "myString", String.class)
		);
	}

	@Test
	void setInstanceFieldValue() {
		assertEquals(
				"Carl",
				ReflectionTestUtils.setInstanceFieldValue(this, "myString", "Bob", String.class),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionTestUtils.setInstanceFieldValue(this, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(this, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getInstanceFieldValue(this, "myString", String.class)
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(this, "myString", "NotNull", String.class),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				42,
				ReflectionTestUtils.setInstanceFieldValue(this, "myInt", Integer.valueOf(99), int.class),
				"Should return the previous value"
		);
		assertEquals(
				99,
				ReflectionTestUtils.setInstanceFieldValue(this, "myInt", 100, int.class),
				"Should return the previous value"
		);
		assertThrows(RuntimeException.class,
				() -> ReflectionTestUtils.setInstanceFieldValue(this, "myInt", null, int.class),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				43,
				ReflectionTestUtils.setInstanceFieldValue(this, "myInteger", Integer.valueOf(999), Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectionTestUtils.setInstanceFieldValue(this, "myInteger", 1000, Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				1000,
				ReflectionTestUtils.setInstanceFieldValue(this, "myInteger", null, Integer.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getInstanceFieldValue(this, "myInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(this, "myInteger", 1001, Integer.class)
		);

		//
		// on the subclass
		Subclass sub = new Subclass();
		assertEquals(
				"SubCarl",
				ReflectionTestUtils.setInstanceFieldValue(sub, "myString", "Bob", String.class),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionTestUtils.setInstanceFieldValue(sub, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(sub, "myString", null, String.class),
				"Should return the previous value"
		);
	}

	@Test
	void getStaticFieldValue() {
		assertEquals(
				"Static",
				ReflectionTestUtils.getStaticFieldValue(this.getClass(), "myStaticString", String.class)
		);
		assertEquals(
				542,
				ReflectionTestUtils.getStaticFieldValue(this.getClass(), "myStaticInt", int.class)
		);
		assertEquals(
				543,
				ReflectionTestUtils.getStaticFieldValue(this.getClass(), "myStaticInteger", Integer.class)
		);

		//
		// on the subclass
		assertEquals(
				"SubStatic",
				ReflectionTestUtils.getStaticFieldValue(Subclass.class, "myStaticString", String.class)
		);
	}

	@Test
	void setStaticFieldValue() {
		assertEquals(
				"Static",
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticString", "Bob"),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getStaticFieldValue(this.getClass(), "myStaticString", String.class)
		);
		assertNull(
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticString", "NotNull"),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				542,
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticInt", Integer.valueOf(999)),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticInt", 1000),
				"Should return the previous value"
		);
		assertThrows(RuntimeException.class,
				() -> ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticInt", null),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				543,
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", Integer.valueOf(9999)),
				"Should return the previous value"
		);
		assertEquals(
				9999,
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", 10000),
				"Should return the previous value"
		);
		assertEquals(
				10000,
				(Integer) ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getStaticFieldValue(this.getClass(), "myStaticInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectionTestUtils.setStaticFieldValue(this.getClass(), "myStaticInteger", 10001)
		);

		//
		// on the subclass
		assertEquals(
				"SubStatic",
				ReflectionTestUtils.setStaticFieldValue(Subclass.class, "myStaticString", "Bob"),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionTestUtils.setStaticFieldValue(Subclass.class, "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.setStaticFieldValue(Subclass.class, "myStaticString", null),
				"Should return the previous value"
		);
	}

	@Test
	void getWritableField() {
		//Most test cases are already implicit in the other tests

		assertThrows(IllegalArgumentException.class,
				() -> ReflectionTestUtils.getWritableField(this.getClass(), "IDONOTEXIST")
		);

	}

	@Test
	void getTypes() {
		//Most test cases are already implicit in the other tests

		assertEquals(0, ReflectionTestUtils.getTypes().length);
		assertEquals(0, ReflectionTestUtils.getTypes(null).length);

		assertThrows(RuntimeException.class,
				() -> ReflectionTestUtils.getTypes("", null)
		);
	}

	@Test
	void getFieldValueTest() {
		Field msField = ReflectionTestUtils.getWritableField(this.getClass(), "myString");
		assertEquals("Carl", ReflectionTestUtils.getFieldValue(msField, this));

		try {
			msField.setAccessible(false);
			assertThrows(
					RuntimeException.class,
					() -> ReflectionTestUtils.getFieldValue(msField, this));
		} finally {
			msField.setAccessible(true);
		}
	}

	@Test
	void setFieldValueTest() {
		Field msField = ReflectionTestUtils.getWritableField(this.getClass(), "myString");
		assertEquals("Carl", ReflectionTestUtils.setFieldValue(msField, this, "Bob"));

		try {
			msField.setAccessible(false);
			assertThrows(
					RuntimeException.class,
					() -> ReflectionTestUtils.setFieldValue(msField, this, "wont work"));
		} finally {
			msField.setAccessible(true);
		}
	}

	@Test
	void getClassByNameTest() {
		Class<?> thisClass = ReflectionTestUtils.getClassByName(
				"org.yarnandtail.andhow.testutil.ReflectionTestUtilsTest");

		assertSame(this.getClass(), thisClass);

		assertThrows(RuntimeException.class,
				() -> ReflectionTestUtils.getClassByName("I.dont.exist.Something"));
	}
}