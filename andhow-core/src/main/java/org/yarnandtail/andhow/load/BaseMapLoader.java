package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;

import java.util.*;

/**
 * Loads values from a Map<String, String>.
 *
 */
public abstract class BaseMapLoader extends BaseLoader implements ReadLoader {

	protected boolean unknownPropertyAProblem = true;

	public LoaderValues load(PropertyConfigurationInternal appConfigDef,
			ValidatedValuesWithContext existingValues, Map<String, String> nameValueMap) {

		if (nameValueMap != null && !nameValueMap.isEmpty()) {
			ArrayList<ValidatedValue> values = new ArrayList();
			ProblemList<Problem> problems = new ProblemList();

			Set<Map.Entry<String, String>> entries = nameValueMap.entrySet();

			for(Map.Entry<String, String> entry : entries) {
				if (entry.getKey() != null) {
					attemptToAdd(appConfigDef, values, problems, entry.getKey(), entry.getValue());
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

	// false is a reasonable default, since its only a special case (the command line loader) that
	// would ever set this true.
	@Override
	public boolean isFlaggable() { return false; }

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

}
