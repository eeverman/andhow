package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.util.*;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;

/**
 * A utility loader that is used internally to put fixed values into the effective
 * list of configured values.
 * 
 * This loader does not trim incoming values for String type properties - they are
 * assumed to already be in final form.
 * This loader considers it a problem to be passed unrecognized properties
 * and will throw a RuntimeException if that happens, though this can be set
 * false to allow fixed values set via name to be ignored if the name is not
 * recognized.
 * 
 * @author eeverman
 */
public class FixedValueLoader extends BaseLoader implements ReadLoader {

	protected boolean unknownPropertyAProblem = true;

	protected List<PropertyValue> values;

	protected List<KeyObjectPair> keyObjectPairValues;

	public FixedValueLoader() {
	}

	/**
	 * Set property values as PropertyValues, which require live
	 * references to each Property.
	 * Values set in this way are additive to properties set via
	 * setKopValues and duplicate properties between the
	 * two will nominally be considered duplicates.
	 *
	 * @param values
	 */
	public void setPropertyValues(List<PropertyValue> values) {
		this.values = values == null ? null : new ArrayList<>(values);
	}

	/**
	 * @deprecated Use {@code FixedValueLoader.setPropertyValues(List<PropertyValue>)} instead.
	 * @param values
	 */
	@Deprecated
	public void setPropertyValues(PropertyValue... values) {
		this.setPropertyValues(values != null ? Arrays.asList(values) : null);
	}

	/**
	 * Set property values as KeyObjectPair's.
	 * Values set in this way are additive to properties set via
	 * setPropertyValues and duplicate properties between the
	 * two will nominally be considered duplicates.
	 *
	 * @param values
	 */
	public void setKeyObjectPairValues(List<KeyObjectPair> values) {
		this.keyObjectPairValues = values == null ? null : new ArrayList<>(values);
	}

	@Override
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef, ValidatedValuesWithContext existingValues) {

		if (values == null) {
			values = new ArrayList<>();
		}
		if (keyObjectPairValues == null) {
			keyObjectPairValues = new ArrayList<>();
		}

		List<ValidatedValue> vvs = new ArrayList(values.size());
		ProblemList<Problem> problems = new ProblemList();

		//Add all the PropertyValue's.  The Property and value references are 'live',
		//so lots of potential errors are not possible, however, the value type may
		//not match the Property, so use 'attemptToAdd' to verify.
		values.stream().forEach(
				v -> this.attemptToAdd(appConfigDef, vvs, problems, v.getProperty(), v.getValue())
		);

		//Add all the KeyObjectPairs
		keyObjectPairValues.stream().forEach(
				kop -> this.attemptToAdd(appConfigDef, vvs, problems, kop.getName(), kop.getValue())
		);

		return new LoaderValues(this, vvs, problems);
		
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}
	
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
		if (values != null) {
			values.clear();
		}
		if (keyObjectPairValues != null) {
			keyObjectPairValues.clear();
		}
	}
	
}
