package org.yarnandtail.andhow.compile;

import java.util.*;

/**
 *
 * @author ericeverman
 */
public abstract class PropertyDeclarationRegistration {
	
	
	public abstract Class<?> getRootClass();
	
	public abstract List<Registration> getPropertyRefs();
	
	public String getCanonicalRootName() {
		return getRootClass().getCanonicalName();
	}
	
	
	
	public List<OldRegistration> getRegistrations() {
		
		List<OldRegistration> registrations = new ArrayList();
		ArrayList<String> propNames = new ArrayList();
		List<Registration> props = getPropertyRefs();
		
		String currentInnerPath = "";
		

		for (Registration propRef : props) {
			
			if (propRef.innerPath != null) {
				
				//This is either the first entry or we just changed inner paths
				//so need to store all previous props
				if (propNames.size() > 0) {
					registrations.add(new OldRegistration(
							getCanonicalRootName() + currentInnerPath,
							propNames.toArray(new String[propNames.size()])));
					propNames.clear();
				} else {
					//this is just the first entry
				}
				
				currentInnerPath = "$" + String.join("$", propRef.innerPath);
			}	
			
			propNames.add(propRef.getPropName());
		
		}
		
		//Catch the last set of props where there was no inner propName change
		if (propNames.size() > 0) {
			registrations.add(new OldRegistration(
					getCanonicalRootName() + currentInnerPath,
					propNames.toArray(new String[propNames.size()])));
		}
		
		return registrations;
		
	}
	

	/**
	 * A List of Registrations with simplified add methods that make it
	 * efficient (source code wise) to add Registrations.
	 * 
	 * Registrations that share the same inner path (that is, the same nested
	 * inner class parents) can just be added with no inner path specified and
	 * they will be assumed to use the same path as the previous entry.
	 * 
	 * Since the source code that calls these add methods is generated and
	 * potentially verbose, its nice to have an efficient way to do it.
	 * 
	 * This class is modifiable and it is assumed that a new list is generated
	 * each time it is requested, ie, an instance is constructed directly in
	 * a 'get' method.  The get method will likely only be called once during its
	 * lifecycle.
	 */
	public static class RegistrationList extends ArrayList<Registration> {

		final String classCanonName;
		Registration lastReg;
		
		public RegistrationList(String classCanonName) {
			this.classCanonName = classCanonName;
		}

		
		/**
		 * Adds a registration using the innerPath specified in the passed registration.
		 * 
		 * Even if the passed registration has a null or empty inner path, it
		 * will be assumed to be correct, meaning a root property.
		 * 
		 * @param reg
		 * @return 
		 */
		@Override
		public boolean add(Registration reg) {
			lastReg = reg;
			return super.add(reg);
		}

		/**
		 * Adds a property registration with the same path as the previously
		 * added one.
		 * 
		 * If there is no previous registration, it is assumed to be a root
		 * (within the current top level class) proerty.
		 * 
		 * @param name
		 * @return 
		 */
		public boolean add(String name) {
			
			Registration reg = null;
			
			if (lastReg != null) {
				reg = new Registration(classCanonName, name, lastReg.getInnerPath());
			} else {
				reg = new Registration(classCanonName, name);
			}
			
			lastReg = reg;
			return super.add(reg);
		}
		
		/**
		 * Adds a property registration with a specified path.  
		 * 
		 * Following calls to add(propName) will use this same path.
		 * 
		 * @param name
		 * @param innerPath
		 * @return 
		 */
		public boolean add(String name, String... innerPath) {
			
			Registration reg = null;
			
			if (lastReg != null) {
				reg = new Registration(classCanonName, name, lastReg.getInnerPath());
			} else {
				reg = new Registration(classCanonName, name);
			}
			
			lastReg = reg;
			return super.add(reg);
		}

		
	}
	
	public static class Registration {
		final String classCanonName;
		final String[] innerPath;
		final String propName;
		
		Registration(String classCanonName, String propName) {
			this.classCanonName = classCanonName;
			this.propName = propName;
			innerPath = null;
		}
		
		Registration(String classCanonName, String propName, String... innerPathNesting) {
			this.classCanonName = classCanonName;
			this.propName = propName;
			innerPath = innerPathNesting;
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
		public String getRootCanonName() {
			return classCanonName;
		}
		
		/**
		 * The name of the AndHow property, which is the name of the variable it
		 * is assigned to where it is constructed.
		 * 
		 * @return 
		 */
		public String getPropName() {
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
		 * <li><code>getRootCanonName</code> would return <code>Foo</code>'s canonical path</li>
		 * <li><code>getInnerPath</code> would return the array of <code>"Bar", "Baz"</code></li>
		 * <li><code>getPropName</code> would return <code>"Boo"</code></li>
		 * </ul>
		 * 
		 * @return The inner path array, in order from top level to leaf level.  May be null or empty.
		 */
		public String[] getInnerPath() {
			return innerPath;
		}
		
		/**
		 * The AndHow canonical name of the property, which is the root canonical
		 * name plus the elements of the inner path and the property name, all
		 * separated by dots.
		 * 
		 * @return 
		 */
		public String getPropertyCanonName() {
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
		 * @return 
		 */
		public String getParentJavaCanonName() {
			if (innerPath != null && innerPath.length > 0) {
				return classCanonName + "$" + String.join("$", innerPath);
			} else {
				return classCanonName;
			}
		}


	}
	
	public static class OldRegistration {
		String classCanonName;
		String[] propNames;
		
		public OldRegistration(String classCanonicalName, String... propertyNames) {
			this.classCanonName = classCanonicalName;
			this.propNames = propertyNames;
		}
		
		public String getClassCanonicalName() {
			return classCanonName;
		}
		
		public List<String> getPropertyNames() {
			return Arrays.asList(propNames);
		}
	}
}
