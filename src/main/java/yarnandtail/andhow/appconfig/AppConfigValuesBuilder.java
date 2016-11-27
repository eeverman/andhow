package yarnandtail.andhow.appconfig;

import yarnandtail.andhow.appconfig.AppConfigValuesProduction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.Loader;

/**
 *
 * @author eeverman
 */
public class AppConfigValuesBuilder implements AppConfigValues {
	
	/** List of maps of values that were loaded by each loader */
	private final List<Map<ConfigPoint<?>, Object>> loadedValues = new ArrayList();
	
	/** List of loaders, corresponding to the loadedValues list */
	private final List<Loader> loaders = new ArrayList();
	
	public AppConfigValuesBuilder() {}

	@Override
	public Object getValue(ConfigPoint<?> point) {
		for (Map<ConfigPoint<?>, Object> map : loadedValues) {
			if (map.containsKey(point)) {
				return map.get(point);
			}
		}
		
		return null;
	}

	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		for (Map<ConfigPoint<?>, Object> map : loadedValues) {
			if (map.containsKey(point)) {
				return true;
			}
		}
		return false;
	}
	
	public void addValues(Loader loader, Map<ConfigPoint<?>, Object> values) {
		if (values != null && values.size() > 0) {
			loadedValues.add(values);
			loaders.add(loader);
		}
	}
	
	/**
	 * Returns all the values loaded by the passed loader.
	 * 
	 * Note that these values may not be the final effective values because they
	 * may have been overridden by values loaded by earlier loaders.
	 * 
	 * @see getEffectiveValuesLoadedByLoader for the list of effective values.
	 * @param loader
	 * @return 
	 */
	public Map<ConfigPoint<?>, Object> getAllValuesLoadedByLoader(Loader loader) {
		int index = loaders.indexOf(loader);
		
		if (index > -1) {
			return Collections.unmodifiableMap(loadedValues.get(index));
		} else {
			return Collections.emptyMap();
		}
	}
	
	/**
	 * Returns the map of values that were loaded by the specified load, only if
	 * they are not overridden by earlier loaders.
	 * 
	 * @param loader
	 * @return 
	 */
	public Map<ConfigPoint<?>, Object> getEffectiveValuesLoadedByLoader(Loader loader) {
		int index = loaders.indexOf(loader);
		
		if (index > -1) {
			Map<ConfigPoint<?>, Object> effLoaderValues = new HashMap();

			for (ConfigPoint<?> point : loadedValues.get(index).keySet()) {
				if (! isValueSetBeforeLoader(point, index)) {
					effLoaderValues.put(point, loadedValues.get(index).get(point));
				}
			}
			
			return effLoaderValues;
			
		} else {
			return Collections.emptyMap();
		}
		
	}
	
	/**
	 * Returns true if the ConfigPoint is set by a loader prior to the laoder
	 * speced by the loaderIndex
	 */
	protected boolean isValueSetBeforeLoader(ConfigPoint<?> point, int loaderIndex) {

		for (int i = 0; i < loaderIndex; i++) {
			if (loadedValues.get(i).containsKey(point)) {
				return true;
			}
		}
		
		return false;
	}
	
	public AppConfigValuesProduction build() {
		Map<ConfigPoint<?>, Object> finalValues = new HashMap();
		
		for (Map<ConfigPoint<?>, Object> map : loadedValues) {
			for (ConfigPoint<?> point : map.keySet()) {
				
				if (! finalValues.containsKey(point)) {
					finalValues.put(point, map.get(point));
				}
			}
		}
		
		return new AppConfigValuesProduction(finalValues);
	}
	
	
}
