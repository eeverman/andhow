package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.load.std.StdFixedValueLoader;

import java.util.*;

/**
 * The runtime environment seen by {@link Loader}s when they load values.
 * <p>
 * Some of the places Loaders load values from are one-of-a-kind / unique resources, such as system
 * properties, environment variables, command line arguments, etc..  Rather than inject each
 * loader with it's needed resources, the LoaderEnvironment makes them available to all loaders.
 * This interface also simplifies testing by providing a central place to set the environment
 * Loaders will see.
 * <p>
 * Note that there are two, parallel 'fixedValue' systems: name and Property.  These are two
 * equally valid ways to set a fixed value, but they cannot be reconciled until the AndHow
 * initialization is complete to resolve names.
 */
public interface LoaderEnvironment {

	/**
	 * Returns a string map view of the current system environment.
	 * <p>
	 * Nominally the same as {@code System.getenv()}, but may be customized for testing.
	 *
	 * @return The environment as a map of variable names to values.  Never null, but possibly empty.
	 */
	public Map<String, String> getEnvironmentVariables();

	/**
	 * Returns a snapshot of current system properties.
	 * <p>
	 * Nominally the same as {@code System.getProperties()}, but may be customized for testing.
	 *
	 * @return A snapshot of the system properties, as a Map<String, String>.  Never null, but possibly empty.
	 */
	public Map<String, String> getSystemProperties();

	/**
	 * Returns the command line argument strings passed to the {@code main(String[] args)} method,
	 * as a List, if they are available.
	 * <p>
	 * Its possible that cmd line arguments are passed to the application but not available here.
	 * This is because AndHow has no way to intercept cmd line arguments, so application code needs
	 * to pass them in to AndHow, which may not have been done
	 * (See how-to details in {@link org.yarnandtail.andhow.load.std.StdMainStringArgsLoader}).
	 * <p>
	 * Nominally this returns the same as the cmd line arguments converted to a List,
	 * but may be customized for testing.
	 *
	 * @return A list of the command line arguments if available, or an empty List.  Never null.
	 */
	public List<String> getCmdLineArgs();

	/**
	 * Returns a Map of hard-coded / fixed values for some properties referenced by name and set by
	 * application code prior to AndHow initialization.
	 * <p>
	 * The referenced Properties and values returned by this method and {@link #getFixedPropertyValues()}
	 * are intended to be loaded by the {@link StdFixedValueLoader}, which nominally loads prior to any
	 * other loader, effectively making the value non-configurable.
	 *
	 * @return A map of property names (canonical or aliases) to property values,
	 * which may be Strings or the destination type of the named Property.
	 * Never null, but possibly empty.
	 */
	public Map<String, Object> getFixedNamedValues();

	/**
	 * Returns a List of hard-coded / fixed values for some properties in the form of a List of
	 * {@link PropertyValue}s, set application code prior to AndHow initialization.
	 * <p>
	 * The referenced Properties and values returned by this method and {@link #getFixedNamedValues()}
	 * are intended to be loaded by the {@link StdFixedValueLoader}, which nominally loads prior to any
	 * other loader, effectively making the value non-configurable.
	 *
	 * @return A list of PropertyValue's.  Never null, but possibly empty.
	 */
	public List<PropertyValue<?>> getFixedPropertyValues();
}
