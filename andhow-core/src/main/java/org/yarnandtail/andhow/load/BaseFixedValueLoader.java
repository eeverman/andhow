package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;

import java.util.*;

/**
 * Abstract utility loader that can load fixed values into the effective
 * list of configured values.
 * <p>
 * To implement a non-abstract class, override the
 * {@link #load(PropertyConfigurationInternal, LoaderEnvironment, ValidatedValuesWithContext)}
 * method with a all to {@link #load(PropertyConfigurationInternal, Map, List)} and supply your own
 * values for one or both of the two collections.
 * <p>
 * By default, this loader does not trim incoming values for String type properties and will
 * throw a RuntimeException if passed unrecognized properties.  Both of these behaviours
 * can be changed the return values of {@link #isTrimmingRequiredForStringValues} and
 * {@link #isUnknownPropertyAProblem}.
 *
 */
public abstract class BaseFixedValueLoader extends BaseLoader implements ReadLoader {

	protected boolean unknownPropertyAProblem = true;

	public BaseFixedValueLoader() {
	}

	@Override
	public LoaderValues load(final PropertyConfigurationInternal runtimeDef,
			final LoaderEnvironment environment, final ValidatedValuesWithContext existingValues) {

		return load(runtimeDef, environment.getFixedNamedValues(), environment.getFixedPropertyValues());
	}

	/**
	 * Load from the passed collections of property values.
	 * <p>
	 * All arguements must be non-null.
	 *
	 * @param runtimeDef The definition of all known Properties and naming metadata
	 * @param fixedNamedValues Fixed values referenced by name
	 * @param fixedPropertyValues Fixed values reference by Property reference
	 * @return The Property values loaded by this loader and/or the problems discovered while
	 * 		attempting to load those Property values.
	 */
	public LoaderValues load(
			PropertyConfigurationInternal runtimeDef,
			Map<String, Object> fixedNamedValues, List<PropertyValue<?>> fixedPropertyValues) {

		List<ValidatedValue> vvs = new ArrayList<>(fixedNamedValues.size() + fixedPropertyValues.size());
		ProblemList<Problem> problems = new ProblemList();

		//Add all the PropertyValue's.  The Property and value references are 'live',
		//so lots of potential errors are not possible, however, the value type may
		//not match the Property, so use 'attemptToAdd' to verify.

		fixedPropertyValues.stream().forEach(
				v -> this.attemptToAdd(runtimeDef, vvs, problems, v.getProperty(), v.getValue())
		);


		//Add all the named property values

		fixedNamedValues.entrySet().stream().forEach(
				v -> this.attemptToAdd(runtimeDef, vvs, problems, v.getKey(), v.getValue())
		);

		return new LoaderValues(this, vvs, problems);
	}

	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}

	@Override
	public boolean isFlaggable() { return false; }

	@Override
	public String getSpecificLoadDescription() {
		return "a list of fixed values passed in during startup (not dynamically loaded)";
	}

	@Override
	public String getLoaderType() {
		return "FixedValue";
	}

	@Override
	public String getLoaderDialect() {
		return "FromJavaSourceCode";
	}

	@Override
	public void setUnknownPropertyAProblem(boolean isAProblem) {
		unknownPropertyAProblem = isAProblem;
	}

	@Override
	public boolean isUnknownPropertyAProblem() {
		return unknownPropertyAProblem;
	}

	@Override
	public void releaseResources() {
		// Nothing to do
	}

}
