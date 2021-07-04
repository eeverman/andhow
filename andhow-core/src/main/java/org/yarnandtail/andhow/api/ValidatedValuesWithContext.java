package org.yarnandtail.andhow.api;

import java.util.List;

/**
 * Extention of ValidatedValues that adds contextual information.
 * 
 * ValidatedValues has all the needed info to provide values for Properties during runtime.
 * This class provides more metadata, such as where a value was loaded from, if
 * there are Problems encountered during value loading and which values were loaded
 * by which Loader, etc..
 * 
 * During startup, a mutable version of ValueMapWithContext is incrementally loaded
 * with values and reported issues.  After loading is complete, values are copied
 * to an immutable ValidatedValues and ValidatedValuesWithContext.
 * ValidatedValues is used to fetch values as needed, ValidatedValuesWithContext
 * provides metadata on values if needed.
 * 
 * @author eeverman
 */
public interface ValidatedValuesWithContext extends ValidatedValues {
	
	/**
	 * Returns true if any value or loader has any sort of issue (invalid value,
	 * parsing error, etc).
	 * 
	 * @return 
	 */
	boolean hasProblems();
	
	/**
	 * A sequential list of all loaders and all its values, even overwritten ones.
	 * @return 
	 */
	List<LoaderValues> getAllLoaderValues();
	
	/**
	 * All the values loaded by the specified Loader, even if they were overridden
	 * by earlier loaders.
	 * 
	 * This is mostly for reporting and debugging to show the user what the loader
	 * was able to find and load.
	 * 
	 * @param loader
	 * @return May return null if the passed Loader is not in use by AndHow.
	 */
	LoaderValues getAllValuesLoadedByLoader(Loader loader);
	
	/**
	 * The list of Properties and values loaded by the specified loader that
	 * were not overridden by earlier Loaders.  I.e., the values returned are
	 * actually in use.
	 * 
	 * This is mostly for reporting and debugging to show the user what the loader
	 * was able to find and load.
	 * 
	 * @param loader
	 * @return May return null if the passed Loader is not in use by AndHow.
	 */
	LoaderValues getEffectiveValuesLoadedByLoader(Loader loader);
	
	/**
	 * Unmodifiable minimum data for Properties to retrieve their data from.
	 * @return 
	 */
	ValidatedValues getValueMapImmutable();
	
	/**
	 * Unmodifiable minimum data collection to store the structure of how data
	 * was loaded.
	 * 
	 * Implementations may maintain this data in one state while loading and
	 * another once loading is done.
	 * 
	 * @return 
	 */
	ValidatedValuesWithContext getValueMapWithContextImmutable();

}
