package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.Collections;
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
	
	/** Lists of values loaded by each loader */
	private final ArrayList<LoaderValues> structuredValues = new ArrayList();
	
	/** Just the final effective values */
	private final AppConfigValues effectiveValues;
		
	public AppConfigStructuredValuesUnmodifiable(List<LoaderValues> inLoadedValuesList) {
		structuredValues.addAll(inLoadedValuesList);
		structuredValues.trimToSize();
		effectiveValues = super.getUnmodifiableAppConfigValues(structuredValues);
	}

	//
	//These two methods use the AppConfigValues instance b/c it is backed by a HashMap
	@Override
	public <T> T getValue(ConfigPoint<T> point) {
		return effectiveValues.getValue(point);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		return effectiveValues.getEffectiveValue(point);
	}

	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return effectiveValues.isPointPresent(point);
	}
	
	@Override
	public List<LoaderValues> getAllLoaderValues() {
		return Collections.unmodifiableList(structuredValues);
	}
	
	//
	//These methods use list searching and are comparatively slow
	
	@Override
	public LoaderValues getAllValuesLoadedByLoader(Loader loader) {
		return getAllValuesLoadedByLoader(structuredValues, loader);
	}

	@Override
	public LoaderValues getEffectiveValuesLoadedByLoader(Loader loader) {
		return getEffectiveValuesLoadedByLoader(structuredValues, loader);
	}
	
	//
	//Build methods

	@Override
	public AppConfigValues getUnmodifiableAppConfigValues() {
		return effectiveValues;
	}
	
	@Override
	public AppConfigStructuredValues getUnmodifiableAppConfigStructuredValues() {
		return this;
	}
}
