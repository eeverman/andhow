package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;

import java.util.*;

/**
 * Basic abstract utility loader that can load fixed values into the effective
 * list of configured values.
 *
 * The {@link Loader#load(PropertyConfigurationInternal, ValidatedValuesWithContext)} implementation
 * implemented here uses the fixed values from the {@link LoaderEnvironment}, but
 * that can easily be overriden to bring fixed values from somewhere else.
 * This loader does not trim incoming values for String type properties - they are
 * assumed to already be in final form.
 * This loader considers it a problem to be passed unrecognized properties
 * and will throw a RuntimeException if that happens, though this can be set
 * false to allow fixed values set via name to be ignored if the name is not
 * recognized.
 *
 */
public abstract class BaseFixedValueLoader extends BaseLoader implements ReadLoader {

	protected boolean unknownPropertyAProblem = true;

	public BaseFixedValueLoader() {
	}

	@Override
	public LoaderValues load(final PropertyConfigurationInternal runtimeDef, final LoaderEnvironment environment, final ValidatedValuesWithContext existingValues) {
		return load(runtimeDef, existingValues, environment.getFixedNamedValues(), environment.getFixedPropertyValues());
	}

	public LoaderValues load(
			PropertyConfigurationInternal appConfigDef, ValidatedValuesWithContext existingValues,
			Map<String, Object> fixedNamedValues, List<PropertyValue<?>> fixedPropertyValues) {

		List<ValidatedValue> vvs = new ArrayList<>(fixedNamedValues.size() + fixedPropertyValues.size());
		ProblemList<Problem> problems = new ProblemList();

		//Add all the PropertyValue's.  The Property and value references are 'live',
		//so lots of potential errors are not possible, however, the value type may
		//not match the Property, so use 'attemptToAdd' to verify.

		fixedPropertyValues.stream().forEach(
				v -> this.attemptToAdd(appConfigDef, vvs, problems, v.getProperty(), v.getValue())
		);


		//Add all the named property values

		fixedNamedValues.entrySet().stream().forEach(
				v -> this.attemptToAdd(appConfigDef, vvs, problems, v.getKey(), v.getValue())
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
