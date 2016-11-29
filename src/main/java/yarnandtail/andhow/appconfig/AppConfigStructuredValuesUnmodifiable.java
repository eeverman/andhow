package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.AppConfigStructuredValues;
import yarnandtail.andhow.LoaderValues;

/**
 *
 * @author eeverman
 */
public class AppConfigStructuredValuesUnmodifiable extends AppConfigStructuredValuesBase {
	
	/** List of maps of values that were loaded by each loader */
	private final ArrayList<LoaderValues> loadedValuesList = new ArrayList();
	
	/**
	 * Only used for subclasses who might populate the loadedValuesList on their
	 * own.
	 */
	protected AppConfigStructuredValuesUnmodifiable() {}
		
	public AppConfigStructuredValuesUnmodifiable(List<LoaderValues> inLoadedValuesList) {
		loadedValuesList.addAll(inLoadedValuesList);
		loadedValuesList.trimToSize();
	}

	@Override
	public Object getValue(ConfigPoint<?> point) {
		return getValue(loadedValuesList, point);
	}

	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return isPointPresent(loadedValuesList, point);
	}
	
	@Override
	public LoaderValues getAllValuesLoadedByLoader(Loader loader) {
		return getAllValuesLoadedByLoader(loadedValuesList, loader);
	}

	@Override
	public LoaderValues getEffectiveValuesLoadedByLoader(Loader loader) {
		return getEffectiveValuesLoadedByLoader(loadedValuesList, loader);
	}

	@Override
	public AppConfigValues getUnmodifiableAppConfigValues() {
		return getUnmodifiableAppConfigValues(loadedValuesList);
	}
	
	@Override
	public AppConfigStructuredValues getUnmodifiableAppConfigStructuredValues() {
		return this;
	}
}
