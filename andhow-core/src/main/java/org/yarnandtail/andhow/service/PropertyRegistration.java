package org.yarnandtail.andhow.service;

import org.yarnandtail.andhow.util.NameUtil;
import java.util.*;

/**
 * Registration for a single Property, which registers a single {@code Property}
 * with AndHow during startup at run time.
 * 
 * <h3>Property registration background</h3>
 * At compile time, the AndHowCompileProcessor (an annotation Processor), reads
 * user classes and generates a PropertyRegistrar instance for each root class
 * (non-inner class) that contains an AndHow {@code Property}.
 * Matching service files are also generated in the "META-INF/services/"
 * directory so the {@code PropertyRegistrar} instances can be discovered
 * through the {@code java.util.ServiceLoader} mechanism.
 * <p>
 * At run time, the {@code PropertyRegistrarLoader} discovers all
 * {@code PropertyRegistrar} instances.
 * Each {@code PropertyRegistrar} creates a {@code PropertyRegistrationList}
 * instance with a {@code PropertyRegistration} for each {@code Property}
 * present in the jar.
 * 
 * @author ericeverman
 */
public class PropertyRegistration implements Comparable<PropertyRegistration> {

	private final String classCanonName;
	private final String[] innerPath;
	private final String propName;

	/**
	 * Create an instance representing a {@code Property} declared directly in
	 * a root class, not in an inner class.
	 * 
	 * @param classCanonName The canonical name of the root class the {@code Property}
	 * is declared in.
	 * @param propName The declared name of the variable referencing the
	 * {@code Property} declaration.
	 */
	PropertyRegistration(String classCanonName, String propName) {
		this.classCanonName = classCanonName;
		this.propName = propName;
		innerPath = null;
	}
	
	/**
	 * Create an instance representing a {@code Property} declared in an inner
	 * class (or nested inner class or interface) as opposed to an instance
	 * declared directly in a root class.
	 * 
	 * @param classCanonName The canonical name of the root class containing the
	 * inner class or nested inner class.
	 * @param propName The declared name of the variable referencing the
	 * {@code Property} declaration.
	 * @param innerPathNesting The name or names of the nested inner classes
	 * and/or interfaces that contain the {@code Property} declaration.  The
	 * path order is in order from the inner class that is declared directly in
	 * the root class to the inner class that contains the {@code Property} declaration.
	 */
	PropertyRegistration(String classCanonName, String propName, String... innerPathNesting) {
		this.classCanonName = classCanonName;
		this.propName = propName;
		
		if (innerPathNesting != null && innerPathNesting.length > 0) {
			innerPath = innerPathNesting;
		} else {
			innerPath = null;
		}
	}
	
	/**
	 * Create an instance representing a {@code Property} declared in an inner
	 * class (or nested inner class or interface) as opposed to an instance
	 * declared directly in a root class.
	 * 
	 * @param classCanonName The canonical name of the root class containing the
	 * inner class or nested inner class.
	 * @param propName The declared name of the variable referencing the
	 * {@code Property} declaration.
	 * @param innerPathNesting A list of names of the nested inner classes
	 * and/or interfaces that contain the {@code Property} declaration.  The
	 * path order is in order from the inner class that is declared directly in
	 * the root class to the inner class that contains the {@code Property} declaration.
	 */
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
	 * @return The Java canonical name of the root class containing the
	 * {@code Property} declaration, or in the case of a {@code Property}
	 * declared in an inner class, the name of the class containing the inner class.
	 */
	public String getCanonicalRootName() {
		return classCanonName;
	}

	/**
	 * The name of the AndHow property, which is the name of the variable it
	 * is assigned to where it is constructed.
	 *
	 * @return The declared name of the variable referencing the
	 * {@code Property} declaration.
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
	 * @return The inner path array, in order from top level to leaf level.  It
	 * may be null if the {@code Property} is declared directly in a root class.
	 */
	public String[] getInnerPath() {
		return innerPath;
	}
	
	/**
	 * The number of nested inner classes / interfaces from the root class to
	 * the inner class containing the AndHow Property.
	 * 
	 * @return The number of steps in the inner path.  A {@code Property} declared
	 * directly in a root class with have a length of zero.
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
	 * @return The full complete canonical name for the {@code Property}, which
	 * uses dots to separate each step.
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
	 * For the inner class {@code InnerClass} of class
	 * {@code com.fastco.ClassName}, the canonical names would be:
	 * <ul>
	 * <li>Java canonical name: {@code com.fastco.ClassName$InnerClass}
	 * <li>AndHow canonical name: {@code com.fastco.ClassName.InnerClass}
	 * </ul>
	 * @return The Java canonical name of the class or inner class containing the
	 * {@code Property}.
	 */
	public String getJavaCanonicalParentName() {
		return NameUtil.getJavaName(classCanonName, innerPath);
	}
	
	/**
	 * The AndHow canonical name of the direct parent of the AndHow Property.
	 *
	 * This may be the canonical name of the root class if there is no
	 * inner path, or it may include an inner path.  The AndHow canonical name
	 * differs from the Java canonical name:  AndHow uses dots to separate all
	 * 'steps' in the classpath while Java uses the dollar sign as a delimiter once
	 * the path goes 'internal'.  The Java canonical name syntax is required if
	 * constructing the class via <code>Class.forName</code>
	 * <p>
	 * For the inner class {@code InnerClass} of class
	 * {@code com.fastco.ClassName}, the canonical names would be:
	 * <ul>
	 * <li>Java canonical name: {@code com.fastco.ClassName$InnerClass}
	 * <li>AndHow canonical name: {@code com.fastco.ClassName.InnerClass}
	 * </ul>
	 * @return The AndHow canonical name of the class or inner class containing the
	 * {@code Property}.
	 */
	public String getCanonicalParentName() {
		return NameUtil.getAndHowName(classCanonName, innerPath);
	}
	
	/**
	 * Equivalent to compareTo for just the root class and its fully qualified path.
	 * 
	 * @param o The {@code PropertyRegistration} to compare to.
	 * @return An int used for sorting that is based only on the root class.
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
	 * @param o The {@code PropertyRegistration} to compare to.
	 * @return int for sorting based only on the inner path.
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
	 * @param o The {@code PropertyRegistration} to compare to.
	 * @return An int used for sorting that provides 'correct' sorting.
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
