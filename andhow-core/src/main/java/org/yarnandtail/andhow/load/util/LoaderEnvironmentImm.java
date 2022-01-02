package org.yarnandtail.andhow.load.util;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.JndiContextWrapper;
import org.yarnandtail.andhow.api.LoaderEnvironment;

import java.util.*;
import java.util.function.Supplier;

/**
 * An immutable LoaderEnvironment implementation.
 *
 */
public class LoaderEnvironmentImm implements LoaderEnvironment {

	// All fields are never null, only potentially empty
	final private Map<String, String> _envVars;
	final private Map<String, String> _sysProps;
	final private List<String> _cmdLineArgs;
	final private Map<String, Object> _fixedNamedValues;
	final private List<PropertyValue<?>> _fixedPropertyValues;

	final private Supplier<JndiContextWrapper> _jndiContextSupplier;

	/**
	 * A new LoaderEnvironmentImm instance.
	 * <p>
	 * All passed values may be null or empty, resulting in that aspect of the environment being empty
	 * for the {@link org.yarnandtail.andhow.api.Loader}s.  Thus, to have the actual values of
	 * {@code System.getenv()} available to the Loaders, the return value of
	 * {@code System.getenv()} must be passed in this constructor, and so on.
	 * <p>
	 * {@code fixedNamedValues} and {@code fixedPropertyValues} are different ways of specifying fixed
	 * values, both of which will be used.  See the related get methods for more details.
	 * This immutable class wraps the passed collections in an unmodifiable wrapper, but does not
	 * copy the values to a new collection.  Thus, callers should not hold references to the passed
	 * collections.
	 *
	 * @param envVars The environment vars (System.getenv()) that the Loaders will see.
	 * @param sysProps The system properties (System.getProperties()) as a Map<String, String> that the Loaders will see.
	 * @param cmdLineArgs The command line arguments (passed to main(String[] args) that the Loaders will see.
	 * @param fixedNamedValues The hard-coded/fixed named Property values that the Loaders will see.
	 * @param fixedPropertyValues The hard-coded/fixed PropertyValues that the Loaders will see.
	 * @param jndiContextSupplier A lambda that returns a JNDI Context
	 */
	public LoaderEnvironmentImm(final Map<String, String> envVars, final Map<String, String> sysProps,
			final List<String> cmdLineArgs, final Map<String, Object> fixedNamedValues,
			List<PropertyValue<?>> fixedPropertyValues,
			Supplier<JndiContextWrapper> jndiContextSupplier) {

		_envVars = (envVars != null)?Collections.unmodifiableMap(envVars):Collections.emptyMap();
		_sysProps = (sysProps != null)?Collections.unmodifiableMap(sysProps):Collections.emptyMap();
		_cmdLineArgs = (cmdLineArgs != null)?Collections.unmodifiableList(cmdLineArgs):Collections.emptyList();
		_fixedNamedValues = (fixedNamedValues != null)?Collections.unmodifiableMap(fixedNamedValues):Collections.emptyMap();
		_fixedPropertyValues = (fixedPropertyValues != null)?Collections.unmodifiableList(fixedPropertyValues):Collections.emptyList();
		_jndiContextSupplier = jndiContextSupplier;

		if (_jndiContextSupplier == null) {
			throw new IllegalArgumentException("The jndiContextSupplier cannot be null. " +
					"To turn off JNDI loading, remove the StdJndiLoader from the loader list, " +
					"or set the JndiContextSupplier to the NoJndiContextSupplier");
		}
	}

	@Override
	public Map<String, String> getEnvironmentVariables() {
		return _envVars;
	}

	@Override
	public Map<String, String> getSystemProperties() {
		return _sysProps;
	}

	@Override
	public List<String> getCmdLineArgs() {
		return _cmdLineArgs;
	}

	@Override
	public Map<String, Object> getFixedNamedValues() {
		return _fixedNamedValues;
	}

	@Override
	public List<PropertyValue<?>> getFixedPropertyValues() { return _fixedPropertyValues;	}

	@Override
	public JndiContextWrapper getJndiContext() {
		return _jndiContextSupplier.get();
	}

}
