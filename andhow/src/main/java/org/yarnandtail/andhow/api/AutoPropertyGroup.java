package org.yarnandtail.andhow.api;

import java.util.List;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 * An AutoPropertyGroup is generated as a proxy for any class containing AndHow Properties.
 * 
 * @author ericeverman
 */
public interface AutoPropertyGroup extends BasePropertyGroup {
	
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
	 * Returns a complete list of all AndHow Properties in this group.
	 *
	 * @return An unmodifiable list of registered properties.
	 */
	List<NameAndProperty> getProperties();
}
