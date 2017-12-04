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
	 * @param <T> The type of Property and value
	 * @param property The property to set a value for
	 * @param value The value to set.
	 * @return
	 */
	<T> C addFixedValue(Property<T> property, T value);
	
	/**
	 * Removes a PropertyValue from the list of fixed values.
	 *
	 * It is not an error to attempt to remove a property that is not in the
	 * current fixed value list.
	 *
	 * @param property A non-null property.
	 * @return
	 */
	C removeFixedValue(Property<?> property);
	
	void build();
}
