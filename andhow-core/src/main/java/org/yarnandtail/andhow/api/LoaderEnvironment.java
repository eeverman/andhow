package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.load.FixedValueLoader;

import java.util.*;

/**
 * The environment for {@link Loader}s when they load values.
 * <p>
 * Loaders can load values from anywhere (JNDI, LDAP, filesystem, etc), however some sources are
 * well defined and make up the environment the Loaders operate within.  Rather than trying to
 * inject those into the each loader as needed, this environment object is available to the
 * Loader to pull the appropriate environment from.
 * <p>
 * This interface also simplifies testing by providing a central place to set the environment that
 * Loaders see when they run.  The two 'fixedValue' systems (name vs Property reference) cannot be
 * reconciled without a complete AndHow initialization, which will be available in the Loader,
 * but not necessarily before.
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
	 * are intended to be loaded by the {@link FixedValueLoader}, which nominally loads prior to any
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
	 * are intended to be loaded by the {@link FixedValueLoader}, which nominally loads prior to any
	 * other loader, effectively making the value non-configurable.
	 *
	 * @return A list of PropertyValue's.  Never null, but possibly empty.
	 */
	public List<PropertyValue<?>> getFixedPropertyValues();
}
