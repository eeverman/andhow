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
 * Testing Utils
 */
public class ReflectionTestUtil {

	public static final String PERMISSION_MSG =
			"There is some type of permissions/access error while trying to access  private fields. "
					+ "Is there a security manager enforcing security during testing?";

	/**
	 * Invokes the named String returning method on the instance via reflection,
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

	public static String stringMethod(Object instance, String name, Object arg, Class<?> type) {
		return (String)invokeMethod(instance, name, new Object[]{arg}, new Class<?>[]{type});
	}

	public static String stringMethod(Object instance, String name, Object... args) {
		return (String)invokeMethod(instance, name, args, getTypes(args));
	}

	public static Object getField(Object instance, String name) {
		List<Field> fields = ReflectionSupport.findFields(instance.getClass(), f -> f.getName().equals(name), HierarchyTraversalMode.BOTTOM_UP);

		if (fields.size() != 1) {
			throw new IllegalArgumentException("Expected to find 1 field, instead found: " + fields.size());
		}

		Optional<Object> optObj = ReflectionSupport.tryToReadFieldValue(fields.get(0), instance).toOptional();

		if (optObj.isPresent()) {
			return optObj.get();
		} else {
			throw new IllegalArgumentException("Unable to return a value");
		}
	}

	public static Object invokeMethod(Object instance, String name, Object[] args, Class<?>[] types) {

		Optional<Method> method = ReflectionSupport.findMethod(instance.getClass(), name, types);

		if (method.isPresent()) {
			return ReflectionSupport.invokeMethod(method.get(), instance, args);
		} else {
			throw new IllegalArgumentException("Method not found");
		}
	}

	public static Class<?>[] getTypes(Object... args) {
		List<? extends Object> types = new ArrayList();

		if (args != null) {
			types = Arrays.stream(args).map(a -> a.getClass()).collect(Collectors.toList());
		}

		return types.toArray(new Class<?>[types.size()]);
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

		try {
			return (T)(field.get(instance));
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
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
		T orgVal = (T)getInstanceFieldValue(instance, fieldName, fieldType);

		Field field = getWritableField(instance.getClass(), fieldName);

		try {
			field.set(instance, fieldValue);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}

		return orgVal;
	}

	/**
	 * Get the value of a static Field.
	 *
	 * @param clazz The class to find the field in
	 * @param fieldName The name of a static field in the class
	 * @param fieldType The type of the field
	 * @return The field value
	 */
	public static <T> T getStaticFieldValue(Class<?> clazz, String fieldName, Class<T> fieldType) {

		Field field = getWritableField(clazz, fieldName);

		try {
			return (T)(field.get(null));
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}

	/**
	 * Set the value of a static Field.
	 *
	 * @param clazz The class to find the field in
	 * @param fieldName The name of a static field in the class
	 * @param value The new value for the field
	 * @return The original field value
	 */
	public static <T> T setStaticFieldValue(Class<?> clazz, String fieldName, T value) {

		T orgVal = (T)getStaticFieldValue(clazz, fieldName, Object.class);

		Field field = getWritableField(clazz, fieldName);

		try {
			field.set(null, value);
		} catch (IllegalAccessException ex) {
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
		try {

			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);

			return field;

		} catch (NoSuchFieldException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}
}
