package org.yarnandtail.andhow.api;

import java.util.List;

/**
 * Public view of configuration and metadata for all known static Properties.
 * 
 * This is the entire domain of Properties defined as static properties
 * (ie the property is defined as a static variable) and on the classpath
 * (or explicitly configured) to be part of AndHow in the current classloader.
 * <br>
 * This definition does not include the actual values configured for the
 * Properties - That is in the ValidatedValues interface.
 * <br>
 * Note that it is not possible to list all known Properties or look them up
 * by name or group.  This is intentional to enforce security.  Property
 * declarations follow Java visibility rules, so you must have the reference to
 * the actual property to look up information or values for that property.  For
 * instance, this property:
 * <br>
 * <code>private static final StrProp FOO = StrProp.builder().build();</code>
 * <br>
 * Is declared as private.  If any part of the public AndHow API provides an
 * alternate path to access the property and read its value, it would bypass
 * the intended visibility of the property.
 * 
 */
public interface StaticPropertyConfiguration {

	/**
	 * Returns all aliases (in and out) for a property.
	 *
	 * This may be different than the aliases requested for the Property, since
	 * the application-level configuration has the ability to add and remove
	 * aliases to mitigate name conflicts.
	 *
	 * @param property The property to fetch naming information for
	 * @return All aliases available for this property.
	 */
	List<EffectiveName> getAliases(Property<?> property);

	/**
	 * Returns the canonical name of a registered property.
	 *
	 * If the property is not registered, null is returned.
	 * @param prop The property to get the canonical name for
	 * @return The canonical name
	 */
	String getCanonicalName(Property<?> prop);

	/**
	 * Finds the Group containing the specified Property.
	 *
	 * @param prop The property to get the PropertyGroup for
	 * @return May return null if the Property is not in any group, or during
	 * construction, if the group has not finished registering all of its properties.
	 */
	GroupProxy getGroupForProperty(Property<?> prop);

	/**
	 * Defines how names are created for Properties.
	 *
	 * @return The NamingStrategy in use.
	 */
	NamingStrategy getNamingStrategy();

}
