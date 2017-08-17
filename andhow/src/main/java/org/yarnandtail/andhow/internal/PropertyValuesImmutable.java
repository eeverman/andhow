package org.yarnandtail.andhow.internal;

import java.util.HashMap;
import java.util.Map;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.PropertyValues;

/**
 * Immutable implementation of PropertyValues.
 * 
 * @author eeverman
 */
public class PropertyValuesImmutable implements PropertyValues {
	
	/** 
	 * All the Properties and associated values registered and actually in use,
	 * meaning that a values was specified by the user in some way.
	 */
	private final Map<Property<?>, Object> loadedValues = new HashMap();
	

	public PropertyValuesImmutable(Map<Property<?>, Object> loadedValues) {
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
