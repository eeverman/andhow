package org.yarnandtail.andhow.internal;

import java.util.HashMap;
import java.util.Map;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.ValidatedValues;

/**
 * Immutable implementation of ValidatedValues.
 * 
 * @author eeverman
 */
public class ValidatedValuesImmutable implements ValidatedValues {
	
	/** 
	 * All the Properties and associated values registered and actually in use,
	 * meaning that a values was specified by the user in some way.
	 */
	private final Map<Property<?>, Object> loadedValues = new HashMap();
	

	public ValidatedValuesImmutable(Map<Property<?>, Object> loadedValues) {
		this.loadedValues.putAll(loadedValues);
	}

	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		return prop.getValueType().cast(loadedValues.get(prop));
	}
	
	@Override
	public <T> T getValue(Property<T> prop) {
		if (isExplicitlySet(prop)) {
			return prop.getValueType().cast(loadedValues.get(prop));
		} else {
			return prop.getDefaultValue();
		}
	}

	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return loadedValues.containsKey(prop);
	}
	
}
