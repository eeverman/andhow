package org.yarnandtail.andhow.api;

import java.util.List;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 * A GroupProxy is generated as a proxy for any class containing AndHow Properties.
 * Properties are grouped by classes or interfaces, often nesting them
 * to form groups.
 * 
 * @author ericeverman
 */
public interface GroupProxy {
	
	/**
	 * Returns the AndHow canonical name of the class this group is proxying.
	 * 
	 * 
	 * This name is dot separated, including the package and nested inner classes.
	 * It does not follow the Java convention of separating inner classes using
	 * dollar signs.
	 * 
	 * For a class with the full name 'org.bigcorp.MyClass', an inner class named
	 * 'MyInner' would have the canonical name 'org.bigcorp.MyClass.MyInner'.
	 *
	 * @return The canonical name of the group.
	 */
	String getCanonicalName();
	
	/**
	 * Returns the Java canonical name of the class this group is proxying.
	 * 
	 * This name follows the Java convention which is to dot separate the package
	 * and class name, but to use dollar signs ($) to separate any inner classes.
	 * 
	 * For a class with the full name 'org.bigcorp.MyClass', an inner class named
	 * 'MyInner' would have the Java canonical name 'org.bigcorp.MyClass$MyInner'.
	 *
	 * @return The canonical name of the group.
	 */
	String getJavaCanonicalName();
	
	/**
	 * The proxied class or interface containing Properties that this proxy wraps.
	 * 
	 * @return 
	 */
	Class<?> getProxiedGroup();
	
	/**
	 * Returns a complete list of all AndHow Properties in this group.
	 *
	 * @return An unmodifiable list of registered properties.
	 */
	List<NameAndProperty> getProperties();
	
	/**
	 * The simple name of a Property in this Group.
	 * 
	 * The simple name has no package or containing class name, it is just the
	 * name of the variable the Property was assigned to at creation.
	 * 
	 * If the passed memberProperty is not a member of this Group, null is returned.
	 * 
	 * @param memberProperty
	 * @return 
	 */
	String getSimpleName(Property<?> memberProperty);
	
	/**
	 * The complete canonical name of a Property in this Group.
	 * 
	 * The canonical name includes the package and all nested inner classes
	 * separated by dots, ending with the name of the variable the Property was
	 * assigned to at creation.
	 * 
	 * If the passed memberProperty is not a member of this Group, null is returned.
	 * 
	 * @param memberProperty
	 * @return 
	 */
	String getCanonicalName(Property<?> memberProperty);
	
	/**
	 * Returns true if this is a group created for user properties, which is
	 * the typical case.
	 * 
	 * Returns false if this is an internally created group for loader properties
	 * or groups used to configure AndHow itself.  In some reports it is useful
	 * to differentiate the groups and properties the user actually created vs
	 * the ones that are just part of the system.
	 * 
	 * @return 
	 */
	boolean isUserGroup();
}
