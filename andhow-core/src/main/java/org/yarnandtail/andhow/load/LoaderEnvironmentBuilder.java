package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;

import java.util.*;

/**
 * A mutable builder implementation of {@link LoaderEnvironment}.
 *
 */
public class LoaderEnvironmentBuilder implements LoaderEnvironment {

	// All fields are never null, only potentially empty
	final protected Map<String, String> _envVars = new HashMap<>();
	final protected Map<String, String> _sysProps = new HashMap<>();
	final protected List<String> _cmdLineArgs = new ArrayList();
	final protected Map<String, Object> _fixedNamedValues = new HashMap<>();
	final protected List<PropertyValue<?>> _fixedPropertyValues = new ArrayList<>();

	// If true, replace empty collections w/ canonical values in toImmutable().
	protected boolean _replaceEmptyEnvVars = true;
	protected boolean _replaceEmptySysProps = true;

	/**
	 * Set the environment vars, overriding any previously set values.
	 * <p>
	 * Setting any values at all results in replacing the system provided env. vars.
	 *
	 * @param envVars The new env vars to use
	 */
	public void setEnvVars(Map<String, String> envVars) {
		_envVars.clear();
		_envVars.putAll(envVars);
	}

	/**
	 * Set the java system properties, overriding any previously set values.
	 * <p>
	 * Setting any values at all results in replacing the system provided sys. props.
	 *
	 * @param sysProps The new sys props to use
	 */
	public void setSysProps(Map<String, String> sysProps) {
		_sysProps.clear();
		_sysProps.putAll(sysProps);
	}

	/**
	 * Set the commandline arguments, overriding any previously set values.
	 * <p>
	 * @param commandLineArgs The new cmd line args to use
	 */
	public void setCmdLineArgs(String[] commandLineArgs) {
		_cmdLineArgs.clear();
		if (commandLineArgs != null && commandLineArgs.length > 0) {
			_cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
		}
	}

	/**
	 * Set the fixed / hardcoded named property values, overriding any previously set values.
	 * <p>
	 * FixedNamedValues and FixedPropertyValues are handled separately, but accomplish the same
	 * thing.  Both result in setting the value of the reference Property to a value in code.
	 * {@code setFixedNamedValues} will only affect named values (it does not overwrite
	 * FixedPropertyValues).  See {@link LoaderEnvironment#getFixedNamedValues()}.
	 *
	 * @param fixedVals A map of fixed values, using either canonical or alias names for keys
	 * 		and correctly typed objects or Strings that can be parsed to the correct type for
	 * 		the referenced Property.  If null or empty, the resulting fixedNamedValue map will be empty.
	 */
	public void setFixedNamedValues(Map<String, Object> fixedVals) {
		_fixedNamedValues.clear();
		if (fixedVals != null) {
			_fixedNamedValues.putAll(fixedVals);
		}
	}


	/**
	 * Sets a fixed, non-configurable value for a Property.
	 *
	 * @param <T> The type of Property and value
	 * @param property The property to set a value for
	 * @param value The value to set.
	 * @return
	 */
	public <T> void addFixedValue(Property<T> property, T value) {

		if (property == null || value == null) {
			throw new IllegalArgumentException("The property and value must be non-null");
		}

		//simple check for duplicates (doesn't consider _fixedNamedValues)
		for (PropertyValue pv : _fixedPropertyValues) {
			if (property.equals(pv.getProperty())) {
				throw new IllegalArgumentException("A fixed value for this property has been assigned twice.");
			}
		}

		PropertyValue pv = new PropertyValue(property, value);
		_fixedPropertyValues.add(pv);
	}

	/**
	 * Removes a fixed Property value set <em>only</em> via addFixedValue(Property<T>, T value) or
	 * {@link #setFixedPropertyValues(List)}.
	 *
	 * It is not an error to attempt to remove a property that is not in this fixed value list.
	 *
	 * @param property A non-null property.
	 * @return The fixed value previously associated with this Property or null if there was none.
	 */
	public Object removeFixedValue(Property<?> property) {
		Iterator<PropertyValue<?>> it = _fixedPropertyValues.iterator();

		while (it.hasNext()) {
			PropertyValue<?> pv = it.next();
			if (pv.getProperty().equals(property)) {
				Object value = pv.getValue();
				it.remove();
				return value;
			}
		}

		return null;
	}


	/**
	 * Sets a fixed, non-configurable value for a named Property.
	 *
	 * @param propertyNameOrAlias The canonical or alias name of Property, which is trimmed to null.
	 * @param value The Object value to set, which must match the type of the Property.
	 * @return
	 * @throws IllegalArgumentException if the name trims to null, the value is null,
	 * 	or the name is already associated w/ a value (use remove).
	 */
	public void addFixedValue(String propertyNameOrAlias, final Object value) {

		propertyNameOrAlias = TextUtil.trimToNull(propertyNameOrAlias);

		if (propertyNameOrAlias == null || value == null) {
			throw new IllegalArgumentException(
					"The property name must contain a non-empty name and value must be non-null");
		}

		//Simple check for duplicates (doesn't consider aliases or _fixedPropertyValues)
		if (_fixedNamedValues.containsKey(propertyNameOrAlias)) {
			throw new IllegalArgumentException(
					"A fixed value for the Property '" + propertyNameOrAlias + "' has been assigned twice.");
		}

		_fixedNamedValues.put(propertyNameOrAlias, value);
	}


	/**
	 * Removes a Property value set <em>only</em> via addFixedValue(String name, Object value)
	 * or {@link #setFixedNamedValues(Map)}.
	 *
	 * Note that to successfully remove a fixed value from this list, the name must exactly
	 * match the name used to set the property via addFixedValue(String, Object).  Since
	 * Properties can have aliases, you must know the exact name to set the property.
	 * <p>
	 * It is not an error to attempt to remove a property that is not in this fixed value list,
	 * or to attempt to remove a property value that does not exist - these are just no-ops.
	 *
	 * @param propertyNameOrAlias The name or alias of a property.
	 * @return The value previously associated w/ the name.
	 */
	public Object removeFixedValue(final String propertyNameOrAlias) {
		return _fixedNamedValues.remove(propertyNameOrAlias);
	}

	/**
	 * Set the fixed / hardcoded property values, overriding any previously set values.
	 * <p>
	 * FixedNamedValues and FixedPropertyValues are handled separately, but accomplish the same
	 * thing.  Both result in setting the value of the reference Property to a value in code.
	 * {@code setFixedPropertyValues} will only affect PropertyValues (it does not overwrite
	 * FixedNamedValues).  See {@link LoaderEnvironment#getFixedPropertyValues()}.
	 *
	 * @param fixedVals A list of fixed PropertyValue's.  If null or empty, the resulting
	 * 		fixedPropertyValue list will be empty.
	 */
	public void setFixedPropertyValues(List<PropertyValue<?>> fixedVals) {
		_fixedPropertyValues.clear();
		if (fixedVals != null) {
			_fixedPropertyValues.addAll(fixedVals);
		}
	}

	public boolean isReplaceEmptyEnvVars() {
		return _replaceEmptyEnvVars;
	}

	public void setReplaceEmptyEnvVars(final boolean replaceEmptyEnvVars) {
		_replaceEmptyEnvVars = replaceEmptyEnvVars;
	}

	public boolean isReplaceEmptySysProps() {
		return _replaceEmptySysProps;
	}

	public void setReplaceEmptySysProps(final boolean replaceEmptySysProps) {
		_replaceEmptySysProps = replaceEmptySysProps;
	}

	public LoaderEnvironmentImm toImmutable() {
		Map<String, String> envVars = (_envVars.isEmpty() && _replaceEmptyEnvVars)?
				System.getenv() : _envVars;

		if (_sysProps.isEmpty() && _replaceEmptySysProps) {
			return new LoaderEnvironmentImm(
					envVars, System.getProperties(), _cmdLineArgs, _fixedNamedValues, _fixedPropertyValues
			);
		} else {
			return new LoaderEnvironmentImm(
					envVars, _sysProps, _cmdLineArgs, _fixedNamedValues, _fixedPropertyValues
			);
		}
	}

	//
	// The LoaderEnvironment interface

	@Override
	public Map<String, String> getEnvironmentVariables() { return _envVars;	}

	@Override
	public Map<String, String> getSystemProperties() { return _sysProps; }

	@Override
	public List<String> getCmdLineArgs() {	return _cmdLineArgs;	}

	@Override
	public Map<String, Object> getFixedNamedValues() { return _fixedNamedValues;	}

	@Override
	public List<PropertyValue<?>> getFixedPropertyValues() { return _fixedPropertyValues;	}

}
