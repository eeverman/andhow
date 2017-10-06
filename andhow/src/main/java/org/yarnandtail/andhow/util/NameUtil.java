package org.yarnandtail.andhow.util;

import java.util.List;

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
	 * Note:  This is NOT THE SAME as getJavaCanonicalParentName() plus the
	 * property name.  Java uses dollar signs to separate inner classes instead
	 * of dots.
	 *
	 * @param classCanonName The fully qualified name of the top level class
	 * @param propName The simple name of the property (the name of the variable
	 *		it is assigned to at construction).
	 * @param innerPath Any inner classes containing the property within the
	 *		top level class, in order from outer to inner.
	 * @return
	 */
	public static String getCanonicalPropertyName(String classCanonName, String propName, List<String> innerPath) {
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
	 * Note:  This is NOT THE SAME as getJavaCanonicalParentName() plus the
	 * property name.  Java uses dollar signs to separate inner classes instead
	 * of dots.
	 *
	 * @param classCanonName The fully qualified name of the top level class
	 * @param propName The simple name of the property (the name of the variable
	 *		it is assigned to at construction).
	 * @param innerPath Any inner classes containing the property within the
	 *		top level class, in order from outer to inner.
	 * @return
	 */
	public static String getCanonicalPropertyName(String classCanonName, String propName, String... innerPath) {
		if (innerPath != null && innerPath.length > 0) {
			return classCanonName + "." + String.join(".", innerPath) + "." + propName;
		} else {
			return classCanonName + "." + propName;
		}
	}

	/**
	 * The Java canonical name of the direct parent of the AndHow Property.
	 *
	 * This may be the canonical name of the root class if there is no
	 * inner path, or it may include an inner path.  Java canonical names
	 * which include inner classes use the dollar sign as a delimiter once
	 * the path goes 'internal' and that syntax is required if constructing
	 * the class via <code>Class.forName</code>
	 * <p>
	 * Example path: <code>com.fastco.ClassName$InnerClass</code>
	 * @param classCanonName
	 * @param innerPath
	 * @return
	 */
	public static String getJavaCanonicalParentName(String classCanonName, String[] innerPath) {
		if (innerPath != null && innerPath.length > 0) {
			return classCanonName + "$" + String.join("$", innerPath);
		} else {
			return classCanonName;
		}
	}

	/**
	 * The Java canonical name of the direct parent of the AndHow Property.
	 *
	 * This may be the canonical name of the root class if there is no
	 * inner path, or it may include an inner path.  Java canonical names
	 * which include inner classes use the dollar sign as a delimiter once
	 * the path goes 'internal' and that syntax is required if constructing
	 * the class via <code>Class.forName</code>
	 * <p>
	 * Example path: <code>com.fastco.ClassName$InnerClass</code>
	 * @param classCanonName
	 * @param innerPath
	 * @return
	 */
	public static String getJavaCanonicalParentName(String classCanonName, List<String> innerPath) {
		if (innerPath != null && innerPath.size() > 0) {
			return classCanonName + "$" + String.join("$", innerPath);
		} else {
			return classCanonName;
		}
	}

}
