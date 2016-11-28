package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.AppConfigStructuredValues;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.LoaderValues.PointValue;

/**
 *
 * @author eeverman
 */
public class AppConfigStructuredValuesImpl implements AppConfigStructuredValues {
	
	/** List of maps of values that were loaded by each loader */
	protected final ArrayList<LoaderValues> loadedValuesList = new ArrayList();
	
	/**
	 * Only used for subclasses who might populate the loadedValuesList on their
	 * own.
	 */
	protected AppConfigStructuredValuesImpl() {}
		
	public AppConfigStructuredValuesImpl(List<LoaderValues> inLoadedValuesList) {
		loadedValuesList.addAll(inLoadedValuesList);
		loadedValuesList.trimToSize();
	}

	@Override
	public Object getValue(ConfigPoint<?> point) {
		return loadedValuesList.stream().filter(lv -> lv.isPointPresent(point))
				.map(lv -> lv.getValue(point)).findFirst().orElse(null);
	}

	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return loadedValuesList.stream().anyMatch(pv -> pv.isPointPresent(point));
	}
	
	@Override
	public LoaderValues getAllValuesLoadedByLoader(Loader loader) {
		return loadedValuesList.stream().filter(lv -> lv.getLoader().equals(loader)).findFirst().get();
	}

	@Override
	public LoaderValues getEffectiveValuesLoadedByLoader(Loader loader) {
		LoaderValues allLoaderValues = getAllValuesLoadedByLoader(loader);
		if (allLoaderValues != null) {
			
			ArrayList<LoaderValues.PointValue> effValues = new ArrayList(allLoaderValues.getValues());
			
			for (LoaderValues lvs : loadedValuesList) {
				
				//only looking for loaders before the specified one
				if (lvs.getLoader().equals(loader)) break;	
				
				//remove 
				effValues.removeIf(pv -> lvs.isPointPresent(pv.getPoint()));
			}
			
			return new LoaderValues(loader, effValues);
			
		} else {
			return null;
		}
		
	}

	@Override
	public AppConfigValues getAppConfigValues() {
		
		Map<ConfigPoint<?>, Object> effValues = new HashMap();
		
		for (LoaderValues lvs : loadedValuesList) {
			for (PointValue pv : lvs.getValues()) {
				effValues.putIfAbsent(pv.getPoint(), pv.getValue());
			}
			
		}
		
		
		return new AppConfigValuesImpl(effValues); 
	}
	
	
}
