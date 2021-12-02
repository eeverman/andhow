package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;

import java.util.*;

/**
 * A mutable builder implementation of {@link LoaderEnvironment}.
 *
 */
public class LoaderEnvironmentBuilder implements LoaderEnvironment {

	// All fields are never null, only potentially empty
	final Map<String, String> _envVars = new HashMap<>();
	final Map<String, String> _sysProps = new HashMap<>();
	final List<String> _mainArgs = new ArrayList();
	final Map<String, Object> _fixedNamedValues = new HashMap<>();
	final List<PropertyValue<?>> _fixedPropertyValues = new ArrayList<>();


	public void setEnvVars(Map<String, String> envVars) {
		_envVars.clear();
		_envVars.putAll(envVars);
	}

	public void setSysProps(Map<String, String> sysProps) {
		_sysProps.clear();
		_sysProps.putAll(sysProps);
	}

	public void setMainArgs(List<String> args) {
		_mainArgs.clear();
		_mainArgs.addAll(args);
	}

	public void setFixedNamedValues(Map<String, Object> fixedVals) {
		_fixedNamedValues.clear();
		_fixedNamedValues.putAll(fixedVals);
	}

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

	public void addFixedValue(final String propertyNameOrAlias, final Object value) {

		if (propertyNameOrAlias == null || value == null) {
			throw new IllegalArgumentException("The property name and value must be non-null");
		}

		//Simple check for duplicates (doesn't consider aliases or _fixedPropertyValues)
		if (_fixedNamedValues.containsKey(propertyNameOrAlias)) {
			throw new IllegalArgumentException(
					"A fixed value for the Property '" + propertyNameOrAlias + "' has been assigned twice.");
		}

		_fixedNamedValues.put(propertyNameOrAlias, value);
	}

	public Object removeFixedValue(final String propertyNameOrAlias) {
		return _fixedNamedValues.remove(propertyNameOrAlias);
	}

	public void setFixedPropertyValues(List<PropertyValue<?>> fixedVals) {
		_fixedPropertyValues.clear();
		_fixedPropertyValues.addAll(fixedVals);
	}
	
	//
	// The LoaderEnvironment interface

	@Override
	public Map<String, String> getEnvironmentVariables() { return _envVars;	}

	@Override
	public Map<String, String> getSystemProperties() { return _sysProps; }

	@Override
	public List<String> getMainArgs() {	return _mainArgs;	}

	public Map<String, Object> getFixedNamedValues() { return _fixedNamedValues;	}

	@Override
	public List<PropertyValue<?>> getFixedPropertyValues() { return _fixedPropertyValues;	}

}
