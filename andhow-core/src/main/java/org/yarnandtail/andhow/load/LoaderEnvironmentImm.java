package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.LoaderEnvironment;

import java.util.*;

/**
 * An immutable LoaderEnvironment implementation.
 *
 */
public class LoaderEnvironmentImm implements LoaderEnvironment {

	// All fields are never null, only potentially empty
	final Map<String, String> _envVars;
	final Map<String, String> _sysProps;
	final List<String> _mainArgs;
	final Map<String, Object> _fixedValues;

	/**
	 * A new LoaderEnvironmentImm instance.
	 * <p>
	 * All passed values may be null or empty, resulting in that aspect of the environment being empty
	 * for the {@link org.yarnandtail.andhow.api.Loader}s.  Thus, to have the actual values of
	 * {@code System.getenv()} available to the Loaders, the return value of
	 * {@code System.getenv()} must be passed in this constructor, and so on.
	 *
	 * @param envVars The environment vars (System.getenv()) that the Loaders will see.
	 * @param sysProps The system properties (System.getProperties()) that the Loaders will see.
	 * @param mainArgs The command line arguments (passed to main(String[] args) that the Loaders will see.
	 * @param fixedValues The hard-coded/fixed Property values that the Loaders will see.
	 */
	public LoaderEnvironmentImm(final Map<String, String> envVars, final Properties sysProps,
			final List<String> mainArgs, final Map<String, Object> fixedValues) {

		_envVars = (envVars != null)?Collections.unmodifiableMap(envVars):Collections.emptyMap();
		_sysProps = buildPropertyMap(sysProps);
		_mainArgs = (mainArgs != null)?Collections.unmodifiableList(mainArgs):Collections.emptyList();
		_fixedValues = (fixedValues != null)?Collections.unmodifiableMap(fixedValues):Collections.emptyMap();
	}

	static Map<String, String> buildPropertyMap(Properties props) {
		if (props != null) {
			final Map<String, String> map = new HashMap<>((props.size() / 2) * 2 + 3, 1);
			props.entrySet().stream().forEach(
					/* Null keys and values are not allowed in a Properties object */
					e -> map.put(e.getKey().toString(), e.getValue().toString())
			);

			return Collections.unmodifiableMap(map);
		} else {
			return Collections.emptyMap();
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
	public List<String> getMainArgs() {
		return _mainArgs;
	}

	@Override
	public Map<String, Object> getFixedValues() {
		return _fixedValues;
	}
}
