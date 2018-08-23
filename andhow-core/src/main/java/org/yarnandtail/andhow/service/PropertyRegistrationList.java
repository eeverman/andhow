package org.yarnandtail.andhow.service;

import java.util.*;

/**
 * A List of Registrations with simplified add methods that make it
 * efficient (source code wise) to add Registrations.
 * <p>
 * Registrations that share the same inner path (that is, the same nested
 * inner class parents) can just be added with no inner path specified and
 * they will be assumed to use the same path as the previous entry.
 * <p>
 * Since the source code that calls these add methods is generated and
 * potentially verbose, its nice to have an efficient way to do it.
 * <p>
 * This class is modifiable and it is assumed that a new list is generated
 * each time it is requested, ie, an instance is constructed directly in
 * a 'get' method.  The get method will likely only be called once during its
 * lifecycle.
 * 
 * <h4>Property registration background</h4>
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
 */
public class PropertyRegistrationList extends ArrayList<PropertyRegistration> {

	private final String rootCanonName;
	private PropertyRegistration lastReg;

	/**
	 * Construct a new registration list for properties in a specific top level
	 * class.
	 * 
	 * @param classCanonName The fully qualified canonical path of the root class.
	 */
	public PropertyRegistrationList(String classCanonName) {
		this.rootCanonName = classCanonName;
	}

	/**
	 * The root / top level class which contains (either directly or indirectly)
	 * the properties listed within it.
	 * 
	 * @return 
	 */
	public String getRootCanonicalName() {
		return rootCanonName;
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
	public boolean add(PropertyRegistration reg) {
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
	 * @param name Name of the AndHow property, which is the name of the variable it
	 *		is assigned to at construction.
	 * @return
	 */
	public boolean add(String name) {
		PropertyRegistration reg;
		
		if (lastReg != null) {
			reg = new PropertyRegistration(rootCanonName, name, lastReg.getInnerPath());
		} else {
			reg = new PropertyRegistration(rootCanonName, name);
		}
		lastReg = reg;
		return super.add(reg);
	}

	/**
	 * Adds a property registration with a specified path.
	 *
	 * Following calls to add(propName) will use this same path.  Passing an
	 * empty innerPath array is interpreted as a property at the root.
	 *
	 * @param name Name of the AndHow property, which is the name of the variable it
	 *		is assigned to at construction.
	 * @param innerPath
	 * @return
	 */
	public boolean add(String name, String... innerPath) {
		PropertyRegistration reg = new PropertyRegistration(rootCanonName, name, innerPath);
		lastReg = reg;
		return super.add(reg);
	}
	
	/**
	 * Adds a property registration with a specified path.
	 *
	 * Following calls to add(propName) will use this same path.  Passing an
	 * empty innerPath array is interpreted as a property at the root.
	 *
	 * @param name Name of the AndHow property, which is the name of the variable it
	 *		is assigned to at construction.
	 * @param innerPath The 'path' of nested inner class/interfaces from outer to inner
	 * @return
	 */
	public boolean add(String name, List<String> innerPath) {
		PropertyRegistration reg = new PropertyRegistration(rootCanonName, name, innerPath);
		lastReg = reg;
		return super.add(reg);
	}
	
	/**
	 * Sorts in lexagraphical order.
	 */
	public void sort() {
		sort(Comparator.naturalOrder());
	}

}
