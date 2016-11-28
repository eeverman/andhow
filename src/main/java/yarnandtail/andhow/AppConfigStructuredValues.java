package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface AppConfigStructuredValues extends AppConfigValues {
	
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
	 * Read-only minimum data for ConfigPoints to retrieve their data from.
	 * @return 
	 */
	AppConfigValues getAppConfigValues();

}
