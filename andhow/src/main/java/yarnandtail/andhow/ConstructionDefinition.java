package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The defined set and metadata related to the PropertyGroups, Properties and
 * associated names that are in scope for an initialized AndHow instance.
 * 
 * This definition does not include the actual values configured for the
 * Properties or Loaders.  This interface is analogous to the list of declared
 * fields.  The values assigned to those fields is analogous to the ValueMap
 * interface.
 * 
 * @author eeverman
 */
public interface ConstructionDefinition {

	public static final List<Property<?>> EMPTY_PROPERTY_LIST = Collections.unmodifiableList(new ArrayList());
	
	/**
	 * Returns all aliases (in and out) for a property.
	 *
	 * This may be different than the aliases requested for the Property, since
	 * the application-level configuration has the ability to add and remove
	 * aliases to mitigate name conflicts.
	 *
	 * @param property
	 * @return
	 */
	List<Alias> getAliases(Property<?> property);

	/**
	 * Returns the canonical name of a registered property.
	 *
	 * If the property is not registered, null is returned.
	 * @param prop
	 * @return
	 */
	String getCanonicalName(Property<?> prop);

	/**
	 * Finds the PropertyGroup containing the specified Property.
	 *
	 * @param prop
	 * @return May return null if the Property is not in any group, or during
	 * construction, if the group has not finished registering all of its properties.
	 */
	Class<? extends PropertyGroup> getGroupForProperty(Property<?> prop);

	/**
	 * Returns an unmodifiable list of registered properties.
	 *
	 * @return
	 */
	List<Property<?>> getProperties();

	/**
	 * Returns a list of Properties registered in the passed group.
	 * If the group is unregistered or has no properties, an empty list is returned.
	 * @param group
	 * @return
	 */
	List<Property<?>> getPropertiesForGroup(Class<? extends PropertyGroup> group);
	
	/**
	 * Returns an unmodifiable list of all registered groups.
	 *
	 * @return
	 */
	List<Class<? extends PropertyGroup>> getPropertyGroups();

	/**
	 * Finds a registered property by any recognized name, including canonical names
	 * or aliases.
	 * 
	 * Note that this recognizes only classpath style names, not URI style names.
	 * 
	 * Some loaders, like the JndiLoader will read both the classpath name, like
	 * <code>my.property</code> and the URI style name, like <code>my/property</code>.
	 * The URI style name is just a means of reading properties in another system - 
	 * for AndHow the URI style name is not considered an actual name.
	 *
	 * @param name
	 * @return The Property or null if it is not found.
	 */
	Property<?> getProperty(String name);
	
	/**
	 * The list of ExportGroups, which handles exporting property values for use
	 * outside the AndHow framework.
	 * 
	 * An ExportGroup bundles an Exporter implementation with a PropertyGroup to
	 * be exported.  After startup is complete, each Exporter will export its
	 * group as configured.
	 * 
	 * @return 
	 */
	List<ExportGroup> getExportGroups();

}
