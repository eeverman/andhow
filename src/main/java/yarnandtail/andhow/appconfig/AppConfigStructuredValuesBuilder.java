package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.ValueMap;
import yarnandtail.andhow.ValueMapWithContext;

/**
 *
 * @author eeverman
 */
public class AppConfigStructuredValuesBuilder extends AppConfigStructuredValuesBase {
	
	/** List of maps of values that were loaded by each loader */
	private final ArrayList<LoaderValues> loadedValuesList = new ArrayList();
	private boolean problem = false;
	
	public AppConfigStructuredValuesBuilder() {
	}
	
	public void addValues(LoaderValues values) {
		loadedValuesList.add(values);
		if (values.hasProblems()) problem = true;
	}
	
	@Override
	public ValueMapWithContext getUnmodifiableAppConfigStructuredValues() {
		return new AppConfigStructuredValuesUnmodifiable(loadedValuesList);
	}
	
	@Override
	public <T> T getExplicitValue(ConfigPoint<T> point) {
		return getValue(loadedValuesList, point);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		return getEffectiveValue(loadedValuesList, point);
	}

	@Override
	public boolean isExplicitlySet(ConfigPoint<?> point) {
		return isPointPresent(loadedValuesList, point);
	}
	
	@Override
	public List<LoaderValues> getAllLoaderValues() {
		return Collections.unmodifiableList(loadedValuesList);
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
	public ValueMap getUnmodifiableAppConfigValues() {
		return getUnmodifiableAppConfigValues(loadedValuesList);
	}

	@Override
	public boolean hasProblems() {
		return problem;
	}

}
