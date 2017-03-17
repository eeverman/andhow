package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.PropertyValue;
import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.api.LoaderValues;
import org.yarnandtail.andhow.api.ValueMapWithContext;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.Loader;
import java.util.*;
import org.yarnandtail.andhow.*;

/**
 * Shared base implementation for both immutable and mutable versions.
 * 
 * @author eeverman
 */
public abstract class ValueMapWithContextBase implements ValueMapWithContext {
	
	public ValueMapWithContextBase() {
	}

	//
	// implementation independent methods to be used w/ subclasses
	protected final <T> T getValue(List<LoaderValues> valuesList, Property<T> prop) {
		return prop.getValueType().cast(valuesList.stream().filter((LoaderValues lv) -> lv.isExplicitlySet(prop)).
						map((LoaderValues lv) -> lv.getExplicitValue(prop)).findFirst().orElse(null)
		);
	}
	
	protected final <T> T getEffectiveValue(List<LoaderValues> valuesList, Property<T> prop) {
		if (isPropertyPresent(valuesList, prop)) {
			return getValue(valuesList, prop);
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
			ArrayList<PropertyValue> effValues = new ArrayList(allLoaderValues.getValues());
			for (LoaderValues lvs : valuesList) {
				//only looking for loaders before the specified one
				if (lvs.getLoader().equals(loader)) {
					break;
				}
				//remove
				effValues.removeIf((PropertyValue pv) -> lvs.isExplicitlySet(pv.getProperty()));
			}
			return new LoaderValues(loader, effValues, ProblemList.EMPTY_PROBLEM_LIST);
		} else {
			return null;
		}
	}
	
	public ValueMapImmutable buildValueMapImmutable(List<LoaderValues> valuesList) {
		
		Map<Property<?>, Object> effValues = new HashMap();
		
		for (LoaderValues lvs : valuesList) {
			for (PropertyValue pv : lvs.getValues()) {
				effValues.putIfAbsent(pv.getProperty(), pv.getValue());
			}
			
		}
		
		
		return new ValueMapImmutable(effValues); 
	}
	
}
