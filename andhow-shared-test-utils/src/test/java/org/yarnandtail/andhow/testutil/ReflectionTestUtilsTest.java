package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.platform.commons.support.ReflectionSupport;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionTestUtilsTest {
	
	Base base;
	Subclass subclass;

	//
	// Sample private instance and static fields to test against

	static class Base {
		private String myString = "Carl";
		private int myInt = 42;
		private Integer myInteger = 43;

		private static String myStaticString = "Static";
		private static int myStaticInt = 542;
		private static Integer myStaticInteger = 543;

		private static Object getFoo(String arg1, Integer arg2) {
			return "getFooReturn";
		}
		
		private static Object getFooNoArg() {
			return "getFooNoArgReturn";
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
	}
			
	// subcclass of this class that masks some values
	static class Subclass extends Base {
		private String myString = "SubCarl";
		private static String myStaticString = "SubStatic";
		
		private static Object getFoo(String arg1, Integer arg2) {
			return "subGetFooReturn";
		}
	}



	@BeforeEach
	void setUp() {
		base = new Base();
		subclass = new Subclass();
	
	
		base.myString = "Carl";
		base.myInt = 42;
		base.myInteger = 43;
		Base.myStaticString = "Static";
		Base.myStaticInt = 542;
		Base.myStaticInteger = 543;

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
				ReflectionTestUtils.stringMethod(base, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);
	}

	@Test
	void testStringMethodWithSingleParamAndType() {
		assertEquals(
				"BOBtrue",
				ReflectionTestUtils.stringMethod(base, "sampleMethod2",
						true, boolean.class)
		);
	}

	@Test
	void testStringMethodWithSingleArrayOfArguments() {
		assertEquals(
				"MyName5",
				ReflectionTestUtils.stringMethod(base, "sampleMethod3","MyName", 5)
		);
		assertEquals(
				"BOBtrue",
				ReflectionTestUtils.stringMethod(base, "sampleMethod4",true)
		);
	}

	@Test
	void invokeInstanceMethod() {
		assertEquals(
				"MyName4",
				ReflectionTestUtils.invokeInstanceMethod(base, "sampleMethod1",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> ReflectionTestUtils.invokeInstanceMethod(base, "IDONTEXIST",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);
	}
	
	@Test
	void invokeStaticMethod() {
		assertEquals(
				"getFooReturn",
				ReflectionTestUtils.invokeStaticMethod(Base.class, "getFoo",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, Integer.class})
		);
		
		assertEquals(
				"getFooNoArgReturn",
				ReflectionTestUtils.invokeStaticMethod(Base.class, "getFooNoArg",
						new Object[] {},
						new Class<?>[] {})
		);
				
		assertEquals(
				"getFooNoArgReturn",
				ReflectionTestUtils.invokeStaticMethod(Base.class, "getFooNoArg",
						null,
						new Class<?>[] {})
		);
		
		assertThrows(
				RuntimeException.class,
				() -> ReflectionTestUtils.invokeStaticMethod(Base.class, "getFooNoArg", new Object[] {}, null),
				"The type array cannot be null"
		);
		
		assertEquals(
				"subGetFooReturn",
				ReflectionTestUtils.invokeStaticMethod(Subclass.class, "getFoo",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, Integer.class})
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> ReflectionTestUtils.invokeStaticMethod(Subclass.class, "IDONTEXIST",
						new Object[] {"MyName", Integer.valueOf(4)},
						new Class<?>[] {String.class, int.class})
		);
	}
	
	@Test
	void invokeMethodTest() {
		
		Optional<Method> method = ReflectionSupport.findMethod(
				Base.class, "getFoo", new Class<?>[] {String.class, Integer.class});
		
		
		assertEquals(
			"getFooReturn",
			ReflectionTestUtils.invokeMethod(method.get(), null, new Object[] {"MyName", Integer.valueOf(4)})
		);


		assertThrows(
			RuntimeException.class,
			() -> ReflectionTestUtils.invokeMethod(method.get(), null,
					new Object[] {"MyName", "This arg is the wrong type"})
		);
	}


	@Test
	void getInstanceFieldValue() {

		// Directly on class
		assertEquals(
				"Carl",
				ReflectionTestUtils.getInstanceFieldValue(base, "myString", String.class)
		);
		assertEquals(
				42,
				ReflectionTestUtils.getInstanceFieldValue(base, "myInt", int.class)
		);
		assertEquals(
				43,
				ReflectionTestUtils.getInstanceFieldValue(base, "myInteger", Integer.class)
		);

		//On the subclass
		assertEquals(
				"SubCarl",
				ReflectionTestUtils.getInstanceFieldValue(subclass, "myString", String.class)
		);
	}

	@Test
	void setInstanceFieldValue() {
		assertEquals(
				"Carl",
				ReflectionTestUtils.setInstanceFieldValue(base, "myString", "Bob", String.class),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionTestUtils.setInstanceFieldValue(base, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(base, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getInstanceFieldValue(base, "myString", String.class)
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(base, "myString", "NotNull", String.class),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				42,
				ReflectionTestUtils.setInstanceFieldValue(base, "myInt", Integer.valueOf(99), int.class),
				"Should return the previous value"
		);
		assertEquals(
				99,
				ReflectionTestUtils.setInstanceFieldValue(base, "myInt", 100, int.class),
				"Should return the previous value"
		);
		assertThrows(RuntimeException.class,
				() -> ReflectionTestUtils.setInstanceFieldValue(base, "myInt", null, int.class),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				43,
				ReflectionTestUtils.setInstanceFieldValue(base, "myInteger", Integer.valueOf(999), Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectionTestUtils.setInstanceFieldValue(base, "myInteger", 1000, Integer.class),
				"Should return the previous value"
		);
		assertEquals(
				1000,
				ReflectionTestUtils.setInstanceFieldValue(base, "myInteger", null, Integer.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getInstanceFieldValue(base, "myInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(base, "myInteger", 1001, Integer.class)
		);

		//
		// on the subclass
		assertEquals(
				"SubCarl",
				ReflectionTestUtils.setInstanceFieldValue(subclass, "myString", "Bob", String.class),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionTestUtils.setInstanceFieldValue(subclass, "myString", null, String.class),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.setInstanceFieldValue(subclass, "myString", null, String.class),
				"Should return the previous value"
		);
	}

	@Test
	void getStaticFieldValue() {
		assertEquals(
				"Static",
				ReflectionTestUtils.getStaticFieldValue(Base.class, "myStaticString", String.class)
		);
		assertEquals(
				542,
				ReflectionTestUtils.getStaticFieldValue(Base.class, "myStaticInt", int.class)
		);
		assertEquals(
				543,
				ReflectionTestUtils.getStaticFieldValue(Base.class, "myStaticInteger", Integer.class)
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
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticString", "Bob"),
				"Should return the previous value"
		);
		assertEquals(
				"Bob",
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticString", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getStaticFieldValue(Base.class, "myStaticString", String.class)
		);
		assertNull(
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticString", "NotNull"),
				"Should return the previous value"
		);

		//
		// int
		assertEquals(
				542,
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticInt", Integer.valueOf(999)),
				"Should return the previous value"
		);
		assertEquals(
				999,
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticInt", 1000),
				"Should return the previous value"
		);
		assertThrows(RuntimeException.class,
				() -> ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticInt", null),
				"Cannot set a primitive null"
		);

		//
		// Integer
		assertEquals(
				543,
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticInteger", Integer.valueOf(9999)),
				"Should return the previous value"
		);
		assertEquals(
				9999,
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticInteger", 10000),
				"Should return the previous value"
		);
		assertEquals(
				10000,
				(Integer) ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticInteger", null),
				"Should return the previous value"
		);
		assertNull(
				ReflectionTestUtils.getStaticFieldValue(Base.class, "myStaticInteger", Integer.class),
				"get value should agree w/ set value"
		);
		assertNull(
				ReflectionTestUtils.setStaticFieldValue(Base.class, "myStaticInteger", 10001)
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
				() -> ReflectionTestUtils.getWritableField(Base.class, "IDONOTEXIST")
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
		Field msField = ReflectionTestUtils.getWritableField(Base.class, "myString");
		assertEquals("Carl", ReflectionTestUtils.getFieldValue(msField, base));

		try {
			msField.setAccessible(false);
			assertThrows(
					RuntimeException.class,
					() -> ReflectionTestUtils.getFieldValue(msField, base));
		} finally {
			msField.setAccessible(true);
		}
	}

	@Test
	void setFieldValueTest() {
		Field msField = ReflectionTestUtils.getWritableField(Base.class, "myString");
		assertEquals("Carl", ReflectionTestUtils.setFieldValue(msField, base, "Bob"));

		try {
			msField.setAccessible(false);
			assertThrows(
					RuntimeException.class,
					() -> ReflectionTestUtils.setFieldValue(msField, base, "wont work"));
		} finally {
			msField.setAccessible(true);
		}
	}

	@Test
	void getClassByNameTest() {
		Class<?> baseClass = ReflectionTestUtils.getClassByName(
				"org.yarnandtail.andhow.testutil.ReflectionTestUtilsTest$Base");

		assertSame(base.getClass(), baseClass);

		assertThrows(RuntimeException.class,
				() -> ReflectionTestUtils.getClassByName("I.dont.exist.Something"));
	}
}