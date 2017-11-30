package org.yarnandtail.andhow.internal;

import java.util.*;
import org.yarnandtail.andhow.api.*;

/**
 * Shared base implementation for both immutable and mutable versions.
 * 
 * @author eeverman
 */
public abstract class ValidatedValuesWithContextBase implements ValidatedValuesWithContext {
	
	public ValidatedValuesWithContextBase() {
	}

	//
	// implementation independent methods to be used w/ subclasses
	protected final <T> T getExplicitValue(List<LoaderValues> valuesList, Property<T> prop) {
		return prop.getValueType().cast(valuesList.stream().filter((LoaderValues lv) -> lv.isExplicitlySet(prop)).
						map((LoaderValues lv) -> lv.getExplicitValue(prop)).findFirst().orElse(null)
		);
	}
	
	/**
	 * Returns the effective value from the values that are loaded so far.
	 * 
	 * This should follow the same semantics as Property values, but it operates
	 * on a potentially partially loaded list of values.  This is typically
	 * invoked during the load process to get configuration for the loaders
	 * themselves.
	 * 
	 * @param <T>
	 * @param valuesList
	 * @param prop
	 * @return 
	 */
	protected final <T> T getEffectiveValue(List<LoaderValues> valuesList, Property<T> prop) {
		T v = getExplicitValue(valuesList, prop);
		
		if (v != null) {
			return v;
		} else {
			return prop.getDefaultValue();
		}
	}

	protected final boolean isPropertyPresent(List<LoaderValues> valuesList, Property<?> prop) {
		return valuesList.stream().anyMatch((LoaderValues pv) -> pv.isExplicitlySet(prop));
	}

	protected final LoaderValues getAllValuesLoadedByLoader(List<LoaderValues> valuesList, Loader loader) {
		return valuesList.stream().filter((LoaderValues lv) -> lv.getLoader().equals(loader)).findFirst().get();
	}

	public LoaderValues getEffectiveValuesLoadedByLoader(List<LoaderValues> valuesList, Loader loader) {
		LoaderValues allLoaderValues = getAllValuesLoadedByLoader(loader);
		if (allLoaderValues != null) {
			ArrayList<ValidatedValue> effValues = new ArrayList(allLoaderValues.getValues());
			for (LoaderValues lvs : valuesList) {
				//only looking for loaders before the specified one
				if (lvs.getLoader().equals(loader)) {
					break;
				}
				//remove
				effValues.removeIf((ValidatedValue pv) -> lvs.isExplicitlySet(pv.getProperty()));
			}
			return new LoaderValues(loader, effValues, ProblemList.EMPTY_PROBLEM_LIST);
		} else {
			return null;
		}
	}
	
	public ValidatedValuesImmutable buildValueMapImmutable(List<LoaderValues> valuesList) {
		
		Map<Property<?>, Object> effValues = new HashMap();
		
		for (LoaderValues lvs : valuesList) {
			for (ValidatedValue pv : lvs.getValues()) {
				effValues.putIfAbsent(pv.getProperty(), pv.getValue());
			}
			
		}
		
		
		return new ValidatedValuesImmutable(effValues); 
	}
	
}
