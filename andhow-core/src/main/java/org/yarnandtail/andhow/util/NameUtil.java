package org.yarnandtail.andhow.util;

import java.util.List;
import org.yarnandtail.andhow.api.Property;

/**
 *
 * @author ericeverman
 */
public class NameUtil {

	/**
	 * The AndHow canonical name of the property, which is the root canonical
	 * name plus the elements of the inner path and the property name, all
	 * separated by dots.
	 *
	 * Note: This is NOT THE SAME as getJavaName() plus the property name. Java
	 * uses dollar signs to separate inner classes instead of dots.
	 *
	 * @param classCanonName The fully qualified name of the top level class
	 * @param propName The simple name of the property (the name of the variable
	 * it is assigned to at construction).
	 * @param innerPath Any inner classes containing the property within the top
	 * level class, in order from outer to inner.
	 * @return
	 */
	public static String getAndHowName(String classCanonName, String propName, List<String> innerPath) {
		if (innerPath != null && innerPath.size() > 0) {
			return classCanonName + "." + String.join(".", innerPath) + "." + propName;
		} else {
			return classCanonName + "." + propName;
		}
	}

	/**
	 * The AndHow canonical name of the property, which is the root canonical
	 * name plus the elements of the inner path and the property name, all
	 * separated by dots.
	 *
	 * Note: This is NOT THE SAME as getJavaName() plus the property name. Java
	 * uses dollar signs to separate inner classes instead of dots.
	 *
	 * @param classCanonName The fully qualified name of the top level class
	 * @param propName The simple name of the property (the name of the variable
	 * it is assigned to at construction).
	 * @param innerPath Any inner classes containing the property within the top
	 * level class, in order from outer to inner.
	 * @return
	 */
	public static String getAndHowName(String classCanonName, String propName, String... innerPath) {
		if (innerPath != null && innerPath.length > 0) {
			return classCanonName + "." + String.join(".", innerPath) + "." + propName;
		} else {
			return classCanonName + "." + propName;
		}
	}

	/**
	 * The Java canonical name of the direct parent of the AndHow Property.
	 *
	 * This may be the canonical name of the root class if there is no inner
	 * path, or it may include an inner path. Java canonical names which include
	 * inner classes use the dollar sign as a delimiter once the path goes
	 * 'internal' and that syntax is required if constructing the class via
	 * <code>Class.forName</code>
	 * <p>
	 * Example path: <code>com.fastco.ClassName$InnerClass</code>
	 *
	 * @param classCanonName
	 * @param innerPath
	 * @return
	 */
	public static String getJavaName(String classCanonName, String[] innerPath) {
		if (innerPath != null && innerPath.length > 0) {
			return classCanonName + "$" + String.join("$", innerPath);
		} else {
			return classCanonName;
		}
	}

	/**
	 * The Java canonical name of the direct parent of the AndHow Property.
	 *
	 * This may be the canonical name of the root class if there is no inner
	 * path, or it may include an inner path. Java canonical names which include
	 * inner classes use the dollar sign as a delimiter once the path goes
	 * 'internal' and that syntax is required if constructing the class via
	 * <code>Class.forName</code>
	 * <p>
	 * Example path: <code>com.fastco.ClassName$InnerClass</code>
	 *
	 * @param classCanonName
	 * @param innerPath
	 * @return
	 */
	public static String getJavaName(String classCanonName, List<String> innerPath) {
		if (innerPath != null && innerPath.size() > 0) {
			return classCanonName + "$" + String.join("$", innerPath);
		} else {
			return classCanonName;
		}
	}

	/**
	 * The AndHow canonical name of the direct parent of the AndHow Property.
	 *
	 * This may be the canonical name of the root class if there is no inner
	 * path, or it may include an inner path. Unlike Java canonical names,
	 * AndHow canonical paths use dots to separate all steps in the path,
	 * including the inner class (Java canonical paths use the dollar sign as a
	 * delimiter for inner classes).
	 * <p>
	 * Example path: <code>com.fastco.ClassName.InnerClass</code>
	 *
	 * @param classCanonName
	 * @param innerPath
	 * @return
	 */
	public static String getAndHowName(String classCanonName, String[] innerPath) {
		if (innerPath != null && innerPath.length > 0) {
			return classCanonName + "." + String.join(".", innerPath);
		} else {
			return classCanonName;
		}
	}

	/**
	 * The AndHow style name for a class.
	 * 
	 * This is an all dot separated name, including separation for inner classes.
	 * 
	 * This method doesn't add much utility, but clearly documents the different
	 * name styles.
	 * 
	 * @param group
	 * @return 
	 */
	public static String getAndHowName(Class<?> group) {
		return group.getCanonicalName();
	}
	
	/**
	 * The Java style name for a class, usable with Class.forName().
	 * 
	 * This is a dot separated name up to inner classes, which are separated by
	 * dollar signs, which is the Java convention.
	 * 
	 * This method doesn't add much utility, but clearly documents the different
	 * name styles.
	 * 
	 * @param group
	 * @return 
	 */
	public static String getJavaName(Class<?> group) {
		return group.getName();
	}

	/**
	 * Gets the true canonical name for a Property in the group.
	 *
	 * The canonical name is of the form:<br>
	 * [group canonical name].[field name of the Property within the group]<br>
	 * thus, it is require that the Property be a field within the group,
	 * otherwise null is returned.
	 *
	 * Exceptions may be thrown if a security manager blocks access to members.
	 *
	 * Technically the NameingStrategy is in charge of generating names, but the
	 * canonical name never changes and is based on the package path of a
	 * Property within a BasePropertyGroup.
	 *
	 * @param group
	 * @param property
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 */
	public static String getAndHowName(Class<?> group, Property<?> property)
			throws IllegalArgumentException, IllegalAccessException, SecurityException {

		String fieldName = AndHowUtil.getFieldName(group, property);

		if (fieldName != null) {
			return NameUtil.getAndHowName(group) + "." + fieldName;
		} else {
			return null;
		}
	}

}
