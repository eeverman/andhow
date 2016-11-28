package yarnandtail.andhow.appconfig;

import java.util.HashMap;
import java.util.Map;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;

/**
 *
 * @author eeverman
 */
public class AppConfigValuesImpl implements AppConfigValues {
	
	/** All the ConfigPoints and associated values registered and actually in use,
	 * meaning that a values was specified by the user in some way.
	 */
	private final Map<ConfigPoint<?>, Object> loadedValues = new HashMap();
	

	public AppConfigValuesImpl(Map<ConfigPoint<?>, Object> loadedValues) {
		this.loadedValues.putAll(loadedValues);
	}

	@Override
	public Object getValue(ConfigPoint<?> point) {
		return loadedValues.get(point);
	}

	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return loadedValues.containsKey(point);
	}
	
}
