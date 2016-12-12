package yarnandtail.andhow.internal;

import java.util.HashMap;
import java.util.Map;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ValueMap;

/**
 *
 * @author eeverman
 */
public class ValueMapImmutable implements ValueMap {
	
	/** All the ConfigPoints and associated values registered and actually in use,
	 * meaning that a values was specified by the user in some way.
	 */
	private final Map<ConfigPoint<?>, Object> loadedValues = new HashMap();
	

	public ValueMapImmutable(Map<ConfigPoint<?>, Object> loadedValues) {
		this.loadedValues.putAll(loadedValues);
	}

	@Override
	public <T> T getExplicitValue(ConfigPoint<T> point) {
		return point.cast(loadedValues.get(point));
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		if (isExplicitlySet(point)) {
			return point.cast(loadedValues.get(point));
		} else {
			return point.getDefaultValue();
		}
	}

	@Override
	public boolean isExplicitlySet(ConfigPoint<?> point) {
		return loadedValues.containsKey(point);
	}
	
}
