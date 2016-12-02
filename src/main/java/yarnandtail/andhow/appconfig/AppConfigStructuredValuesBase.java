package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.AppConfigStructuredValues;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PointValue;

/**
 *
 * @author eeverman
 */
public abstract class AppConfigStructuredValuesBase implements AppConfigStructuredValues {
	
	public AppConfigStructuredValuesBase() {
	}

	//
	// implementation independent methods to be used w/ subclasses
	protected final <T> T getValue(List<LoaderValues> valuesList, ConfigPoint<T> point) {
		return point.cast(
				valuesList.stream().filter((LoaderValues lv) -> lv.isPointPresent(point)).
						map((LoaderValues lv) -> lv.getValue(point)).findFirst().orElse(null)
		);
	}
	
	protected final <T> T getEffectiveValue(List<LoaderValues> valuesList, ConfigPoint<T> point) {
		if (isPointPresent(valuesList, point)) {
			return getValue(valuesList, point);
		} else {
			return point.getDefaultValue();
		}
	}

	protected final boolean isPointPresent(List<LoaderValues> valuesList, ConfigPoint<?> point) {
		return valuesList.stream().anyMatch((LoaderValues pv) -> pv.isPointPresent(point));
	}

	protected final LoaderValues getAllValuesLoadedByLoader(List<LoaderValues> valuesList, Loader loader) {
		return valuesList.stream().filter((LoaderValues lv) -> lv.getLoader().equals(loader)).findFirst().get();
	}

	public LoaderValues getEffectiveValuesLoadedByLoader(List<LoaderValues> valuesList, Loader loader) {
		LoaderValues allLoaderValues = getAllValuesLoadedByLoader(loader);
		if (allLoaderValues != null) {
			ArrayList<PointValue> effValues = new ArrayList(allLoaderValues.getValues());
			for (LoaderValues lvs : valuesList) {
				//only looking for loaders before the specified one
				if (lvs.getLoader().equals(loader)) {
					break;
				}
				//remove
				effValues.removeIf((PointValue pv) -> lvs.isPointPresent(pv.getPoint()));
			}
			return new LoaderValues(loader, effValues);
		} else {
			return null;
		}
	}
	
	public AppConfigValues getUnmodifiableAppConfigValues(List<LoaderValues> valuesList) {
		
		Map<ConfigPoint<?>, Object> effValues = new HashMap();
		
		for (LoaderValues lvs : valuesList) {
			for (PointValue pv : lvs.getValues()) {
				effValues.putIfAbsent(pv.getPoint(), pv.getValue());
			}
			
		}
		
		
		return new AppConfigValuesUnmodifiable(effValues); 
	}
	
}
