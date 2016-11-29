package yarnandtail.andhow.appconfig;

import java.util.HashMap;
import java.util.Map;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;

/**
 *
 * @author eeverman
 */
public class AppConfigValuesUnmodifiable implements AppConfigValues {
	
	/** All the ConfigPoints and associated values registered and actually in use,
	 * meaning that a values was specified by the user in some way.
	 */
	private final Map<ConfigPoint<?>, Object> loadedValues = new HashMap();
	

	public AppConfigValuesUnmodifiable(Map<ConfigPoint<?>, Object> loadedValues) {
		this.loadedValues.putAll(loadedValues);
	}

	@Override
	public <T> T getValue(ConfigPoint<T> point) {
		return point.cast(loadedValues.get(point));
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		if (isPointPresent(point)) {
			return point.cast(loadedValues.get(point));
		} else {
			return point.getDefaultValue();
		}
	}

	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return loadedValues.containsKey(point);
	}
	
}
