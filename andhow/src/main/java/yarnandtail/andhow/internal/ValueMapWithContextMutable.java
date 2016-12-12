package yarnandtail.andhow.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.ValueMap;
import yarnandtail.andhow.ValueMapWithContext;
import yarnandtail.andhow.Property;

/**
 *
 * @author eeverman
 */
public class ValueMapWithContextMutable extends ValueMapWithContextBase {
	
	/** List of maps of values that were loaded by each loader */
	private final ArrayList<LoaderValues> loadedValuesList = new ArrayList();
	private boolean problem = false;
	
	public ValueMapWithContextMutable() {
	}
	
	public void addValues(LoaderValues values) {
		loadedValuesList.add(values);
		if (values.hasProblems()) problem = true;
	}
	
	@Override
	public ValueMapWithContext getValueMapWithContextImmutable() {
		return new ValueMapWithContextImmutable(loadedValuesList);
	}
	
	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		return getValue(loadedValuesList, prop);
	}
	
	@Override
	public <T> T getEffectiveValue(Property<T> prop) {
		return getEffectiveValue(loadedValuesList, prop);
	}

	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return isPointPresent(loadedValuesList, prop);
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
	public ValueMap getValueMapImmutable() {
		return buildValueMapImmutable(loadedValuesList);
	}

	@Override
	public boolean hasProblems() {
		return problem;
	}

}
