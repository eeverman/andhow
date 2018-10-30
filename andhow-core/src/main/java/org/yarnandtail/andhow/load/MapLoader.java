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
	
	/** The map. */
	protected Map<?, ?> map;
	
	/** The unknown property A problem. */
	protected boolean unknownPropertyAProblem = true;
	
	/**
	 * Instantiates a new map loader.
	 */
	public MapLoader() {
	}
	
	/**
	 * Sets the map.
	 *
	 * @param map the map
	 */
	public void setMap(Map<?, ?> map) {
		this.map = map;
	}
	
	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public Map<?, ?> getMap() {
		return map;
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#load(org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal, org.yarnandtail.andhow.api.ValidatedValuesWithContext)
	 */
	@Override
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef, 
			ValidatedValuesWithContext existingValues) {
		
		Map<?, ?> props = getMap();
		
		if (props != null) {
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
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#getSpecificLoadDescription()
	 */
	@Override
	public String getSpecificLoadDescription() {
		return "Map";
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#isTrimmingRequiredForStringValues()
	 */
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.ReadLoader#setUnknownPropertyAProblem(boolean)
	 */
	@Override
	public void setUnknownPropertyAProblem(boolean isAProblem) {
		unknownPropertyAProblem = isAProblem;
	}

	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.ReadLoader#isUnknownPropertyAProblem()
	 */
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
		map = null;
	}
}
