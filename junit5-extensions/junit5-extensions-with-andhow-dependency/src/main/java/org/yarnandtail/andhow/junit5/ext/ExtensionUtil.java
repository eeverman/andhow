package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;

public class ExtensionUtil {

	private ExtensionUtil() {
		/* NO OP - no instances */
	}


	/**
	 * Copied from JUnit AnnotationUtils
	 *
	 * ref:  AnnotationUtils.findAnnotation(Class<?> clazz, Class<A> annotationType,
	 * 			boolean searchEnclosingClasses)
	 * @param clazz
	 * @param annotationType
	 * @return
	 * @param <A>
	 */
	public static <A extends Annotation> Optional<A> findAnnotation(Class<?> clazz, Class<A> annotationType) {

		Class<?> candidate = clazz;
		while (candidate != null) {
			Optional<A> annotation = AnnotationSupport.findAnnotation(candidate, annotationType);
			if (annotation.isPresent()) {
				return annotation;
			}
			candidate = (isInnerClass(candidate) ? candidate.getEnclosingClass() : null);
		}
		return Optional.empty();
	}

	/**
	 * Is this a non-static innerClass?
	 *
	 * @param clazz
	 * @return
	 */
	public static boolean isInnerClass(Class<?> clazz) {
		return !(Modifier.isStatic(clazz.getModifiers())) && clazz.isMemberClass();
	}

	/**
	 * Throws an exception if the passed classpath does not exist
	 * @param classpath
	 */
	public static boolean isExistingResource(String classpath) {
		return ExtensionUtil.class.getResource(classpath) != null;
	}

	/**
	 * Generate a comprehensive list of classes that includes inner class which might contain
	 * AndHow properties.
	 */
	public static List<Class<?>> stem(List<Class<?>> clazzes) {
		final List<Class<?>> stemmed = new ArrayList<>();
		stemmed.addAll(clazzes);

		int index = 0;

		while (index < stemmed.size()) {
			Class<?> c = stemmed.get(index);

			Arrays.stream(c.getDeclaredClasses()).forEach( cc -> {
					if (Modifier.isStatic(cc.getModifiers())) {
						stemmed.add(cc);
					}
			});

			index++;
		}

		return stemmed;
	}


	public static <A extends Annotation> A findAnnotation(Class<A> annotationClass,
			ExtensionType.Scope scope, ExtensionContext context) {

		if (scope.isClassAnnotation()) {

			//This method will hunt nested classes, and because the AndHow annotations are marked for
			//inheritance, the superclass ones as well.

			//Note:  Could use the JUnit AnnotationSupport.findAnnotation(Class<?>, Class<?>, SearchOption)
			//method here, but its experimental and only added in recent versions of Junit.
			Optional<A> ann = ExtensionUtil.findAnnotation(context.getRequiredTestClass(), annotationClass);

			if (!ann.isPresent()) {
				throw new IllegalStateException("Expected the @" + annotationClass.getName() + " annotation on the '" +
						context.getRequiredTestClass() + "' class, superclass or a parent class for a @Nested test.");
			}

			return ann.get();
		} else if (scope.isMethodAnnotation()) {
			// Operating at the method level, so the annotation should be in the current test method
			Optional<A> ann = AnnotationSupport.findAnnotation(context.getElement(), annotationClass);

			if (!ann.isPresent()) {
				throw new IllegalStateException("Expected the @" + annotationClass.getName() + " annotation on the '" +
						context.getRequiredTestMethod().getName() + "' test method of " + context.getRequiredTestClass());
			}
			return ann.get();
		} else {
			throw new IllegalStateException("Cannot call findAnnotation() if the getExtensionType() returns " +
					"a type that doesn't use TEST_CLASS, EACH_TEST or SINGLE_TEST scope.");
		}
	}

	/**
	 * Expand the passed classpath to be an absolute classpath if it was a relative one.
	 * <p>
	 * A classpath is determined to be a relative path if it does not start with a slash.
	 * In that case, the package of the path is expanded to include the package of the test
	 * on which this extension is used.
	 * If the path contains a slash, but it does not start with a slash, it is an illegal argument.
	 * Other non-allowed characters in a single classpath are illegal, including spaces and commas.
	 * All paths are trimmed of whitespace before processing.
	 * Null paths will throw a NullPointer.
	 * @param classpath The classpath to the properties file the user wants to use to configure AndHow
	 * @param context The test context this extension is being run within
	 * @return
	 */
	public static String expandPath(String classpath, ExtensionContext context) {
		String fullPath = classpath.trim();

		if (! fullPath.startsWith("/")) {

			String pkgName = "";	//empty is correct for default pkg

			if (context.getRequiredTestClass().getPackage() != null) {
				//getPackage() returns null for the default pkg
				pkgName = context.getRequiredTestClass().getPackage().getName();
			}

			String pkgPath = pkgName.replace(".", "/");
			if (pkgPath.length() > 0) pkgPath = "/" + pkgPath;

			fullPath = pkgPath + "/" + fullPath;
		}

		return fullPath;
	}
}
