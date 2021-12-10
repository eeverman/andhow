package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;
import java.util.*;
import org.yarnandtail.andhow.api.*;

/**
 * Loads values from a map.
 *
 */
public class MapLoader extends BaseMapLoader {

	protected Map<String, String> map;

	public void setMap(Map<String, String> map) {
		this.map = map == null ? null : new HashMap<>(map);
	}

	public Map<String, String> getMap() {
		return map;
	}

	@Override
	public LoaderValues load(final PropertyConfigurationInternal runtimeDef, final ValidatedValuesWithContext existingValues) {
		return null;
	}

	@Override
	public LoaderValues load(final PropertyConfigurationInternal runtimeDef,
			final LoaderEnvironment environment, final ValidatedValuesWithContext existingValues) {
		return load(runtimeDef, existingValues, map);
	}

	@Override
	public void releaseResources() {
		map = null;
	}
}
