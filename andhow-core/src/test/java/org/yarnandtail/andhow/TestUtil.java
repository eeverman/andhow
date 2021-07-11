package org.yarnandtail.andhow;

import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.Preconditions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP;

/**
 * Testing Utils
 */
public class TestUtil {

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



}
