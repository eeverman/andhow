package org.yarnandtail.andhow.compile;

import java.util.List;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistration implements Comparable<PropertyRegistration> {

	private final String classCanonName;
	private final String[] innerPath;
	private final String propName;

	PropertyRegistration(String classCanonName, String propName) {
		this.classCanonName = classCanonName;
		this.propName = propName;
		innerPath = null;
	}

	PropertyRegistration(String classCanonName, String propName, String... innerPathNesting) {
		this.classCanonName = classCanonName;
		this.propName = propName;
		
		if (innerPathNesting != null && innerPathNesting.length > 0) {
			innerPath = innerPathNesting;
		} else {
			innerPath = null;
		}
	}
	
	PropertyRegistration(String classCanonName, String propName, List<String> innerPathNesting) {
		this.classCanonName = classCanonName;
		this.propName = propName;
		
		if (innerPathNesting != null && innerPathNesting.size() > 0) {
			innerPath = innerPathNesting.toArray(new String[innerPathNesting.size()]);
		} else {
			innerPath = null;
		}
	}

	/**
	 * The canonical propName of the root class, which is the top level class,
	 * typically one per Java source file (unless someone has stuffed multiple
	 * top level classes into a single file, which is technically allowed).
	 *
	 * An inner class or inner interface would be represented by the inner
	 * path, not by the root canonical propName.
	 *
	 * @return
	 */
	public String getCanonicalRootName() {
		return classCanonName;
	}

	/**
	 * The name of the AndHow property, which is the name of the variable it
	 * is assigned to where it is constructed.
	 *
	 * @return
	 */
	public String getPropertyName() {
		return propName;
	}

	/**
	 * The names of the nested inner classes/interfaces containing the
	 * named property for this registration.
	 * <p>
	 * If a class <code>Foo</code> contained an inner class <code>Bar</code>, which contained an
	 * inner class <code>Baz</code>, which contained an AndHow property declaration
	 * assigned to a variable named <code>Boo</code>:
	 * <ul>
	 * <li><code>getCanonicalRootName</code> would return <code>Foo</code>'s canonical path</li>
	 * <li><code>getInnerPath</code> would return the array of <code>"Bar", "Baz"</code></li>
	 * <li><code>getPropertyName</code> would return <code>"Boo"</code></li>
	 * </ul>
	 *
	 * @return The inner path array, in order from top level to leaf level.  Populated or null.
	 */
	public String[] getInnerPath() {
		return innerPath;
	}
	
	/**
	 * The number of nested inner classes / interfaces from the root class to
	 * the inner class containing the AndHow Property.
	 * 
	 * @return 
	 */
	public int getInnerPathLength() {
		if (innerPath == null) {
			return 0;
		} else {
			return innerPath.length;
		}
	}

	/**
	 * The AndHow canonical name of the property, which is the root canonical
	 * name plus the elements of the inner path and the property name, all
	 * separated by dots.
	 *
	 * @return
	 */
	public String getCanonicalPropertyName() {
		return NameUtil.getCanonicalPropertyName(classCanonName, propName, innerPath);
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
	 * @return
	 */
	public String getJavaCanonicalParentName() {
		return NameUtil.getJavaCanonicalParentName(classCanonName, innerPath);
	}

	/**
	 * Comparison that results in sorting from root properties to the most
	 * nested, incrementally by each inner path step.
	 * 
	 * @param o
	 * @return 
	 */
	@Override
	public int compareTo(PropertyRegistration o) {

		if (getInnerPathLength() == 0) {
			if (o.getInnerPathLength() == 0) {
				return getPropertyName().compareTo(o.getPropertyName());
			} else {
				return -1;
			}
		} else {
			for (int i = 0; i < getInnerPathLength() && i < o.getInnerPathLength(); i++) {
				int comp = innerPath[i].compareTo(o.innerPath[i]);
				
				if (comp != 0) {
					return comp;
				}
			}
			
			return getPropertyName().compareTo(o.getPropertyName());
		}
		
	}
	
}
