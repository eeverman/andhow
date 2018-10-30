package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.util.*;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;

// TODO: Auto-generated Javadoc
/**
 * A utility loader that is used internally to put fixed values into the
 * effective list of values.
 * 
 * This loader does not trim incoming values for String type properties - they
 * are assumed to already be in final form. This loader considers it a problem
 * to be passed unrecognized properties and will throw a RuntimeException if
 * that happens.
 * 
 * @author eeverman
 */
public class FixedValueLoader extends BaseLoader implements ReadLoader {

	/** The unknown property A problem. */
	protected boolean unknownPropertyAProblem = true;

	/** The values. */
	protected List<PropertyValue> values = new ArrayList();

	/**
	 * Instantiates a new fixed value loader.
	 */
	public FixedValueLoader() {
	}

	/**
	 * Sets the property values.
	 *
	 * @param values
	 *            the new property values
	 */
	public void setPropertyValues(List<PropertyValue> values) {
		if (values != null) {
			this.values.addAll(values);
		}
	}

	/**
	 * Sets the property values.
	 *
	 * @param values
	 *            the new property values
	 */
	public void setPropertyValues(PropertyValue... values) {
		if (values != null && values.length > 0) {
			this.values.addAll(Arrays.asList(values));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yarnandtail.andhow.api.Loader#load(org.yarnandtail.andhow.internal.
	 * StaticPropertyConfigurationInternal,
	 * org.yarnandtail.andhow.api.ValidatedValuesWithContext)
	 */
	@Override
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef,
			ValidatedValuesWithContext existingValues) {

		if (values != null && values.size() > 0) {
			List<ValidatedValue> vvs = new ArrayList(values.size());

			for (int i = 0; i < values.size(); i++) {
				ValidatedValue vv = new ValidatedValue(values.get(i).getProperty(), values.get(i).getValue());
				vvs.add(vv);
			}

			return new LoaderValues(this, vvs, ProblemList.EMPTY_PROBLEM_LIST);

		} else {
			return new LoaderValues(this, Collections.emptyList(), ProblemList.EMPTY_PROBLEM_LIST);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yarnandtail.andhow.api.Loader#isTrimmingRequiredForStringValues()
	 */
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yarnandtail.andhow.api.Loader#getSpecificLoadDescription()
	 */
	@Override
	public String getSpecificLoadDescription() {
		return "a list of fixed values passed in by the construction code (not dynamically loaded)";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yarnandtail.andhow.api.Loader#getLoaderType()
	 */
	@Override
	public String getLoaderType() {
		return "FixedValue";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yarnandtail.andhow.api.Loader#getLoaderDialect()
	 */
	@Override
	public String getLoaderDialect() {
		return "FromJavaSourceCode";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.yarnandtail.andhow.api.ReadLoader#setUnknownPropertyAProblem(boolean)
	 */
	@Override
	public void setUnknownPropertyAProblem(boolean isAProblem) {
		unknownPropertyAProblem = isAProblem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yarnandtail.andhow.api.ReadLoader#isUnknownPropertyAProblem()
	 */
	@Override
	public boolean isUnknownPropertyAProblem() {
		return unknownPropertyAProblem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yarnandtail.andhow.load.BaseLoader#releaseResources()
	 */
	@Override
	public void releaseResources() {
		values = null;
	}

}
