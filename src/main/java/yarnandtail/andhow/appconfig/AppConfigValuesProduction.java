package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;

/**
 *
 * @author eeverman
 */
public class AppConfigValuesProduction implements AppConfigValues {
	
	/** List of maps of values that were loaded by each loader */
	private final Map<ConfigPoint<?>, Object> loadedValues = new HashMap();
	

	public AppConfigValuesProduction(Map<ConfigPoint<?>, Object> loadedValues) {
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
