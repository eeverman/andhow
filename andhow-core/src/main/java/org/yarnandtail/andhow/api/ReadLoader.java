package org.yarnandtail.andhow.api;

/**
 * A Read Loader is a loader that reads properties from a resource for which it
 * is possible to enumerate all values.
 * 
 * Compare this to Lookup Loaders which look up values, possibly in external
 * systems and may not be able to (easily) enumerate all values.  It is feasible
 * for ReadLoaders to enforce 'no unrecognized property names', which it is not
 * for LookupLoaders.
 * 
 * @author ericeverman
 */
public interface ReadLoader extends Loader {
	
	/**
	 * If true, an unrecognized property name is considered a problem/error.
	 * 
	 * @param isAProblem 
	 */
	void setUnknownPropertyAProblem(boolean isAProblem);
	
	
	/**
	 * If true, an unrecognized name is considered a problem that will stop 
	 * application startup.
	 * 
	 * This is a safety feature because a unrecognized property name in a
	 * properties file could be a typo.  For other property sources, however,
	 * like System.properties, there are many non-AndHow related values that
	 * are expected to be present, so this rule cannot be enforced.
	 * 
	 * @return 
	 */
	boolean isUnknownPropertyAProblem();
}
