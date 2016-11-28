package yarnandtail.andhow.appconfig;

import yarnandtail.andhow.appconfig.AppConfigValuesImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.AppConfigStructuredValues;
import yarnandtail.andhow.LoaderValues;

/**
 *
 * @author eeverman
 */
public class AppConfigStructuredValuesImpl implements AppConfigStructuredValues {
	
	/** List of maps of values that were loaded by each loader */
	private final List<LoaderValues> loadedValuesList = new ArrayList();
	
	public AppConfigStructuredValuesImpl() {}

	@Override
	public Object getValue(ConfigPoint<?> point) {
		return loadedValuesList.stream().filter(lv -> lv.isPointPresent(point))
				.map(lv -> lv.getValue(point)).findFirst().orElse(null);
	}

	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return loadedValuesList.stream().anyMatch(pv -> pv.isPointPresent(point));
	}
	
	public void addValues(LoaderValues values) {
		loadedValuesList.add(values);
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
	public LoaderValues getAllValuesLoadedByLoader(Loader loader) {

	}
	
	/**
	 * Returns the map of values that were loaded by the specified load, only if
	 * they are not overridden by earlier loaders.
	 * 
	 * @param loader
	 * @return 
	 */
	public LoaderValues getEffectiveValuesLoadedByLoader(Loader loader) {
		int index = loaders.indexOf(loader);
		
		if (index > -1) {
			Map<ConfigPoint<?>, Object> effLoaderValues = new HashMap();

			for (ConfigPoint<?> point : loadedValuesList.get(index).keySet()) {
				if (! isValueSetBeforeLoader(point, index)) {
					effLoaderValues.put(point, loadedValuesList.get(index).get(point));
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
			if (loadedValuesList.get(i).containsKey(point)) {
				return true;
			}
		}
		
		return false;
	}
	
	public AppConfigValuesImpl build() {
		Map<ConfigPoint<?>, Object> finalValues = new HashMap();
		
		for (Map<ConfigPoint<?>, Object> map : loadedValuesList) {
			for (ConfigPoint<?> point : map.keySet()) {
				
				if (! finalValues.containsKey(point)) {
					finalValues.put(point, map.get(point));
				}
			}
		}
		
		return new AppConfigValuesImpl(finalValues);
	}
	
	
}
