package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.util.*;
import org.yarnandtail.andhow.api.*;

/**
 * Loads values from a map.
 * 
 * @author eeverman
 */
public class MapLoader extends BaseLoader implements ReadLoader {

	protected Map<?, ?> map = Collections.emptyMap();

	protected boolean unknownPropertyAProblem = true;
	
	public MapLoader() {
	}
	
	public void setMap(Map<?, ?> map) {
		this.map = map == null ? Collections.emptyMap() : new HashMap<>(map);
	}
	
	public Map<?, ?> getMap() {
		return map;
	}
	
	@Override
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef,
			ValidatedValuesWithContext existingValues) {
		
		Map<?, ?> props = getMap();

		if (!props.isEmpty()) {
			ArrayList<ValidatedValue> values = new ArrayList();
			ProblemList<Problem> problems = new ProblemList();

			Set<?> keys = props.keySet();
			for(Object key : keys) {
				if (key != null) {
					Object val = props.get(key);
					String sVal = (val != null)?val.toString():null;

					attemptToAdd(appConfigDef, values, problems, key.toString(), sVal);
				}
			}

			values.trimToSize();
			return new LoaderValues(this, values, problems);
		} else {
			return new LoaderValues(this);
		}
		
		
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "Map";
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return true;
	}
	
	@Override
	public void setUnknownPropertyAProblem(boolean isAProblem) {
		unknownPropertyAProblem = isAProblem;
	}

	@Override
	public boolean isUnknownPropertyAProblem() {
		return unknownPropertyAProblem;
	}
	
	@Override
	public String getLoaderType() {
		return "Map";
	}
	
	@Override
	public String getLoaderDialect() {
		return null;
	}
	
	@Override
	public void releaseResources() {
		map.clear();
	}
}
