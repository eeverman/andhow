package org.yarnandtail.andhow;

import java.util.List;
import org.yarnandtail.andhow.api.*;

/**
 *
 * @author ericeverman
 */
public interface AndHowConfiguration<C extends AndHowConfiguration> {
	List<Loader> buildLoaders();
	
	List<GroupProxy> getRegisteredGroups();

	NamingStrategy getNamingStrategy();
	
	/**
	 * Sets the command line arguments, removing any previously set commandline args.
	 *
	 * @param commandLineArgs
	 * @return
	 */
	C setCmdLineArgs(String[] commandLineArgs);
	
	
	/**
	 * Sets a fixed, non-configurable value for a Property.
	 *
	 * Property values set in this way use the FixedValueLoader to load values
	 * prior to any other loader. Since the first loaded value for a Property
	 * 'wins', this effectively fixes the value and makes it non-configurable.
	 *
	 * Values specified by the two <code>addFixedValue</code> methods will
	 * through a <code>DuplicatePropertyLoaderProblem</code> if they refer to
	 * the same Property.
	 *
	 * @param <T> The type of Property and value
	 * @param property The property to set a value for
	 * @param value The value to set.
	 * @return
	 */
	<T> C addFixedValue(Property<T> property, T value);
	
	/**
	 * Removes a Property value set <em>only</em> via addFixedValue(Property<T>, T value)
	 * from the list of fixed values.
	 *
	 * It is not an error to attempt to remove a property that is not in this fixed value list.
	 *
	 * @param property A non-null property.
	 * @return
	 */
	C removeFixedValue(Property<?> property);

	/**
	 * Sets a fixed, non-configurable value for a named Property.
	 *
	 * Property values set in this way use the FixedValueLoader to load values
	 * prior to any other loader. Since the first loaded value for a Property
	 * 'wins', this effectively fixes the value and makes it non-configurable.
	 *
	 * Values specified by the two <code>addFixedValue</code> methods will
	 * through a <code>DuplicatePropertyLoaderProblem</code> if they refer to
	 * the same Property.
	 *
	 * @param name The canonical or alias name of Property, which is trimmed to null.
	 * @param value The Object value to set, which must match the type of the Property.
	 * @return
	 */
	C addFixedValue(String name, Object value);

	/**
	 * Removes a Property value set <em>only</em> via addFixedValue(String name, Object value)
	 * from the list of fixed values.
	 *
	 * Note that to successfully remove a fixed value from this list, the name must exactly
	 * match the name used to set the property via addFixedValue(String, Object).  Since
	 * Properties can have aliases, you must know the exact name to set the property.
	 *
	 * It is not an error to attempt to remove a property that is not in this fixed value list,
	 * or to attempt to remove a property value that does not exist - these are just no-ops.
	 *
	 * @param propertyNameOrAlias The name or alias of a property.
	 * @return
	 */
	C removeFixedValue(String propertyNameOrAlias);
	
	void build();
}
