package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	 * @return 
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
	 * @return 
	 */
	LoaderValues getEffectiveValuesLoadedByLoader(Loader loader);
	
	/**
	 * Read-only minimum data for ConfigPoints to retrieve their data from.
	 * @return 
	 */
	AppConfigValues getAppConfigValues();

}
