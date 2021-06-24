package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.util.*;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;

/**
 * A utility loader that is used internally to put fixed values into the effective
 * list of values.
 * 
 * This loader does not trim incoming values for String type properties - they are
 * assumed to already be in final form.
 * This loader considers it a problem to be passed unrecognized properties
 * and will throw a RuntimeException if that happens.
 * 
 * @author eeverman
 */
public class FixedValueLoader extends BaseLoader implements ReadLoader {

	protected boolean unknownPropertyAProblem = true;
	
	protected List<PropertyValue> values = new ArrayList();

	protected List<KOP> kopValues = new ArrayList();
			
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
		if (values != null) {
			this.values.addAll(values);
		}
	}

	/**
	 * Set property values as KOPs.
	 * Values set in this way are additive to properties set via
	 * setPropertyValues and duplicate properties between the
	 * two will nominally be considered duplicates.
	 *
	 * @param values
	 */
	public void setKopValues(List<KOP> values) {
		if (values != null) {
			this.kopValues.addAll(values);
		}
	}

	/**
	 * @deprecated This method duplicates functionality and will
	 * be removed.
	 * @param values
	 */
	public void setPropertyValues(PropertyValue... values) {
		if (values != null && values.length > 0) {
			this.values.addAll(Arrays.asList(values));
		}
	}
	
	@Override
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef, ValidatedValuesWithContext existingValues) {
		
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
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "a list of fixed values passed in by the construction code (not dynamically loaded)";
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
		values = null;
	}
	
}
