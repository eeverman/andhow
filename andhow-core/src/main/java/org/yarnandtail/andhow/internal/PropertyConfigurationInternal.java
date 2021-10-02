package org.yarnandtail.andhow.internal;

import java.util.*;
import org.yarnandtail.andhow.api.ExportGroup;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.PropertyConfiguration;

/**
 * Provides a complete view of all known Properties.
 *
 * This extends the public view of properties from StaticPropertyConfiguration
 * and adds the ability to work the entire list of properties.  This is an
 * internal-only API because that property list must never be made public or it
 * would bypass the security of limited visibility Properties.
 *
 * @see PropertyConfiguration
 */
public interface PropertyConfigurationInternal extends PropertyConfiguration {

	public static final List<Property<?>> EMPTY_PROPERTY_LIST = Collections.unmodifiableList(new ArrayList());


	/**
	 * Returns a complete list of all registered properties.
	 *
	 * @return An unmodifiable list of registered properties.
	 */
	List<Property<?>> getProperties();

	/**
	 * Returns true if any of the registered groups are user groups.
	 *
	 * False would indicate that no user groups were found or explicitly registered.
	 * @return
	 */
	boolean containsUserGroups();

	/**
	 * Finds the Group containing the specified Property.
	 *
	 * @param prop The property to get the PropertyGroup for
	 * @return May return null if the Property is not in any group, or during
	 * construction, if the group has not finished registering all of its properties.
	 */
	GroupProxy getGroupForProperty(Property<?> prop);

	/**
	 * Returns a list of Properties registered in the passed group.
	 *
	 * If the group is unregistered or has no properties, an empty list is returned.
	 *
	 * @param group The group to get Properties for
	 * @return An unmodifiable list of Properties
	 */
	List<Property<?>> getPropertiesForGroup(GroupProxy group);

	/**
	 * Returns a list of all registered groups.
	 *
	 * @return An unmodifiable list of Groups
	 */
	List<GroupProxy> getPropertyGroups();

	/**
	 * Finds a registered property by any recognized classpath style name,
	 * including the canonical name or 'in' aliases.
	 *
	 * Note that this recognizes only classpath style names, not URI style names
	 * such as would be used in JNDI.
	 *
	 * Some loaders, like the JndiLoader will read both the classpath name, like
	 * <code>my.property</code> and the URI style name, like <code>my/property</code>.
	 * The URI style name is just a means of reading properties in another system -
	 * for AndHow the URI style name is not considered an actual name.
	 *
	 * @param classpathStyleName A path to a property in the classpath style.
	 * @return The Property or null if it is not found.
	 */
	Property<?> getProperty(String classpathStyleName);

	/**
	 * The list of ExportGroups, which handles exporting property values for use
	 * outside the AndHow framework.
	 *
	 * An ExportGroup bundles an Exporter implementation with a Group
	 * to be exported.  After startup is complete, each Exporter will export its
	 * group as configured.
	 *
	 * @return An unmodifiable list of export groups.
	 */
	List<ExportGroup> getExportGroups();


}
