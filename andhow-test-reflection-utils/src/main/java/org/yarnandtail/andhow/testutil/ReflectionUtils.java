package org.yarnandtail.andhow.testutil;

import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Generic (in the sense that they don't specifically apply to AndHow) test utilities that
 * access private fields and methods.
 */
public class ReflectionUtils {

	public static final String PERMISSION_MSG =
			"There is some type of permissions/access error - "
					+ "See the underlying error message.";

	/**
	 * Invokes the named String returning instance method on the instance via reflection,
	 * bypassing visibility.
	 *
	 * For some tests, this is an easy way test the internal effect of some actions.
	 * JUnit ReflectionSupport *should* find methods without having to pass types, but
	 * this bug prevents that:
	 * https://github.com/junit-team/junit5/issues/2663
	 *
	 * @param instance The object instance to call the method on.
	 * @param name The name of a String returning method, which must be an instance method.
	 * @param args Arguments to the method
	 * @param types The argument types of the method which must match the method, not the args.
	 * @return The String returned by the method
	 * @throws Exception
	 */
	public static String stringMethod(Object instance, String name, Object[] args, Class<?>[] types) {
			return (String)invokeMethod(instance, name, args, types);
	}

	/**
	 * Invokes the named String returning, single arg, instance method on the instance via reflection,
	 * bypassing visibility.
	 *
	 * @param instance The object instance to call the method on.
	 * @param name The name of a String returning method, which must be an instance method.
	 * @param arg The single argument to pass to the method
	 * @param type Type of the argument, which allos the argument to be null.
	 * @return The return value of the method
	 */
	public static String stringMethod(Object instance, String name, Object arg, Class<?> type) {
		return (String)invokeMethod(instance, name, new Object[]{arg}, new Class<?>[]{type});
	}

	/**
	 * Invoke a String returning instance method with an array of arguments, all of which must
	 * be non-null.
	 *
	 * Note:  Java will auto-box primitives, causing the type to not match the signiture of
	 * a method with primitive arguments.  This method cannot be used for methods with
	 * primative types.  It also cannot be used to pass null arguments, since there is
	 * no way to figure out the types.
	 *
	 * @param instance The object instance to call the method on.
	 * @param name The name of a String returning method, which must be an instance method.
	 * @param args Arguments to the method, which must be all non-null
	 * @return Return value of the invoked method
	 */
	public static String stringMethod(Object instance, String name, Object... args) {
		return (String)invokeMethod(instance, name, args, getTypes(args));
	}

	/**
	 * Invokes the named String returning instance  method on the instance via reflection,
	 * bypassing visibility.
	 *
	 * @param instance The object instance to call the method on.
	 * @param name The name of a String returning method, which must be an instance method.
	 * @param args Arguments to the method
	 * @param types The argument types of the method which must match the method, not the args.
	 * @return The String returned by the method
	 * @return The return value of the method
	 */
	public static Object invokeMethod(Object instance, String name, Object[] args, Class<?>[] types) {

		Optional<Method> method = ReflectionSupport.findMethod(instance.getClass(), name, types);

		if (method.isPresent()) {
			return ReflectionSupport.invokeMethod(method.get(), instance, args);
		} else {
			throw new IllegalArgumentException("Method not found");
		}
	}

	/**
	 * Get the value of an instance Field.
	 *
	 * @param instance The instance to find the field in
	 * @param fieldName The name of a static field in the class
	 * @param fieldType The type of the field
	 * @return The field value
	 */
	public static <T> T getInstanceFieldValue(Object instance, String fieldName, Class<T> fieldType) {
		Field field = getWritableField(instance.getClass(), fieldName);
		return (T) getFieldValue(field, instance);
	}

	/**
	 * Set the fieldValue of an instance Field.
	 *
	 * @param instance The instance to find the field in
	 * @param fieldName The name of a static field in the class
	 * @param fieldValue The new fieldValue for the field
	 * @param fieldType The type of the field (since fieldValue could be null)
	 * @return The original field fieldValue
	 */
	public static <T> T setInstanceFieldValue(Object instance, String fieldName, T fieldValue, Class<T> fieldType) {
		Field field = getWritableField(instance.getClass(), fieldName);
		return (T) setFieldValue(field, instance, fieldValue);
	}

	/**
	 * Get the value of a static Field.
	 *
	 * Unlike an instance field, a static field is not inherited by subclasses.  If you have these
	 * two classes:
	 * <pre>{@code
	 * class A {
	 *   static int classAInt;
	 * }
	 *
	 * class B extends A {
	 *   static int classBInt;
	 * }
	 * }</pre>
	 *
	 * To get the value of {@code classAInt}, you must call
	 * {@code getStaticFieldValue(A.class, "classAInt", int.class)}.  If you call the same method on
	 * class B, the field will not be found.
	 *
	 * @param clazz The class to find the field in (not a subclass of it)
	 * @param fieldName The name of a static field in the class
	 * @param fieldType The type of the field - Really just a convenience so the caller doesn't have
	 *                  to cast the result.  The value may be null as long as there is a compiler known
	 *                  type for the argument.
	 * @return The field value
	 */
	public static <T> T getStaticFieldValue(Class<?> clazz, String fieldName, Class<T> fieldType) {
		Field field = getWritableField(clazz, fieldName);
		return (T) getFieldValue(field, null);
	}


	/**
	 *
	 * Sets the value of a static Field.
	 *
	 * Unlike an instance field, a static field is not inherited by subclasses.  If you have these
	 * two classes:
	 * <pre>{@code
	 * class A {
	 *   static int classAInt;
	 * }
	 *
	 * class B extends A {
	 *   static int classBInt;
	 * }
	 * }</pre>
	 *
	 * To set the value of {@code classAInt}, you must call
	 * {@code setStaticFieldValue(A.class, "classAInt", 42)}.  If you call the same method on
	 * class B, the field will not be found.
	 * <p>
	 * Since static fields must be uniquely named at the class level, primitives and autoboxing of
	 * types is not a problem.
	 *
	 * @param clazz The class to find the field in (not a subclass of it)
	 * @param fieldName The name of a static field in the class
	 * @param value The new value for the field.
	 * @return The field value
	 */
	public static <T> T setStaticFieldValue(Class<?> clazz, String fieldName, T value) {
		Field field = getWritableField(clazz, fieldName);
		return (T) setFieldValue(field, null, value);
	}

	/**
	 * Get the value of a field w/o throwing an Exception that needs to be caught.
	 *
	 * @param field The field to retrieve the value of.
	 * @param instance The object instance the field is a member of, may be null
	 *                 to access static fields.
	 * @return
	 */
	public static Object getFieldValue(Field field, Object instance) {
		try {
			return field.get(instance);
		} catch (Exception ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}

	/**
	 * Get the value of a field w/o throwing an Exception that needs to be caught.
	 *
	 * The value previously assigned to the field is returned.
	 *
	 * @param field The field to retrieve the value of.
	 * @param instance The object instance the field is a member of, may be null
	 * 				to access static fields.
	 * @param value The value to assign, which may be null.
	 * @return The value previously assigned to the field.
	 */
	public static Object setFieldValue(Field field, Object instance, Object value) {

		Object orgVal = getFieldValue(field, instance);

		try {
			field.set(instance, value);
		} catch (Exception ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}

		return orgVal;
	}

	/**
	 * Get a Field (static or instance), make it accessible and only throw a RuntimeException if there is a problem.
	 *
	 * @param clazz The class to find the field in
	 * @param fieldName The name of a static field in the class
	 * @return The field, set accessible.
	 */
	public static Field getWritableField(Class<?> clazz, String fieldName) {
		List<Field> fields = ReflectionSupport.findFields(clazz, f -> f.getName().equals(fieldName), HierarchyTraversalMode.BOTTOM_UP);

		if (fields.size() != 1) {
			throw new IllegalArgumentException("Expected to find 1 field, instead found: " + fields.size());
		}

		Field field = fields.get(0);
		field.setAccessible(true);

		return field;
	}

	/**
	 * Get the types of a list of objects for use as argument types.
	 *
	 * This may be called with no arguments or a single null argument meaning -
	 * I have no arguments.  However, if multiple arguments are passed, they must each be non-null.
	 *
	 * @param args
	 * @return
	 */
	public static Class<?>[] getTypes(Object... args) {
		List<? extends Object> types = new ArrayList();

		if (args != null) {
			types = Arrays.stream(args).map(a -> a.getClass()).collect(Collectors.toList());
		}

		return types.toArray(new Class<?>[types.size()]);
	}
}
