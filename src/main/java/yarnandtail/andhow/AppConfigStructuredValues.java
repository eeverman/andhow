package yarnandtail.andhow;

import java.util.List;

/**
 *
 * @author eeverman
 */
public interface AppConfigStructuredValues extends AppConfigValues {
	
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
	 * @return May return null if the passed Loader is not in use by the AppConfig.
	 */
	LoaderValues getAllValuesLoadedByLoader(Loader loader);
	
	/**
	 * The list of ConfigPoints and values loaded by the specified loader that
	 * were not overridden by earlier Loaders.  I.e., the values returned are
	 * actually in use.
	 * 
	 * This is mostly for reporting and debugging to show the user what the loader
	 * was able to find and load.
	 * 
	 * @param loader
	 * @return May return null if the passed Loader is not in use by the AppConfig.
	 */
	LoaderValues getEffectiveValuesLoadedByLoader(Loader loader);
	
	/**
	 * Unmodifiable minimum data for ConfigPoints to retrieve their data from.
	 * @return 
	 */
	AppConfigValues getUnmodifiableAppConfigValues();
	
	/**
	 * Unmodifiable minimum data collection to store the structure of how data
	 * was loaded.
	 * 
	 * Implementations may maintain this data in one state while loading and
	 * another once loading is done.
	 * 
	 * @return 
	 */
	AppConfigStructuredValues getUnmodifiableAppConfigStructuredValues();

}
