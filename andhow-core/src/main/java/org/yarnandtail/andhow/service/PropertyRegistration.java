package org.yarnandtail.andhow.service;

import org.yarnandtail.andhow.util.NameUtil;
import java.util.*;

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
		return NameUtil.getAndHowName(classCanonName, propName, innerPath);
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
		return NameUtil.getJavaName(classCanonName, innerPath);
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
	public String getCanonicalParentName() {
		return NameUtil.getAndHowName(classCanonName, innerPath);
	}
	
	/**
	 * Equivalent to compareTo for just the root class and its fully qualified path.
	 * 
	 * @param o Comp the classCanonName name of this PropertyRegistration
	 * @return 
	 */
	public int compareRootTo(PropertyRegistration o) {

		if (classCanonName.equals(o.classCanonName)) {
			
			return 0;	//easy
			
		} else {
			//These are Properties from different root classes - check each step of the fully qualified name
			
			String[] thisPath = classCanonName.split("\\.");
			String[] thatPath = o.classCanonName.split("\\.");
			
			int minLen = Math.min(thisPath.length, thatPath.length);
			
			for (int i = 0; i < minLen; i++) {
				
				if (i == (minLen - 1)) {
					//last comparable step
					
					if (thisPath.length < thatPath.length) {
						return -1;
					} else if (thisPath.length > thatPath.length) {
						return 1;
					} else {
						return thisPath[i].compareTo(thatPath[i]);
					}
					
				} else {
					
					int comp = thisPath[i].compareTo(thatPath[i]);
					
					if (comp != 0) {
						return comp;
					}
					
				}
				
			}
			
			return 0;
		}
	}
	

	/**
	 * Equivalent to compareTo for just the inner path class and its fully qualified path.
	 * 
	 * @param o Comp the classCanonName name of this PropertyRegistration
	 * @return 
	 */
	public int compareInnerPathTo(PropertyRegistration o) {

		String[] thisPath = innerPath;
		String[] thatPath = o.innerPath;
		
		
		//Check the null combinations
		if (thisPath == null) {
			if (thatPath == null) {
				return 0;
			} else {
				return -1;
			}
		} else if (thatPath == null) {
			return 1;
		}
		

		int minLen = Math.min(thisPath.length, thatPath.length);

		for (int i = 0; i < minLen; i++) {

			if (i == (minLen - 1)) {
				//last comparable step

				int comp = thisPath[i].compareTo(thatPath[i]);
				
				if (comp != 0) {
					return comp;
				} else {
					
					if (thisPath.length < thatPath.length) {
						return -1;
					} else if (thisPath.length > thatPath.length) {
						return 1;
					} else {
						return 0;
					}
					
				}

			} else {

				int comp = thisPath[i].compareTo(thatPath[i]);

				if (comp != 0) {
					return comp;
				}

			}

		}

		return 0;
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
		
		int comp = compareRootTo(o);
		
		if (comp != 0) {
			return comp;
		}
		
		comp = compareInnerPathTo(o);
		
		if (comp != 0) {
			return comp;
		}
		
		return getPropertyName().compareTo(o.getPropertyName());
		
	}
	
}
