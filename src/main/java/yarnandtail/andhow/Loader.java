package yarnandtail.andhow;

import java.util.Map;
import yarnandtail.andhow.load.LoaderState;

/**
 * Each instance is responsible for loading values from a particular type of source.
 * 
 * Implementations may define a set of ConfigPoints used to control the behavior
 * of the loader, which are returned from getLoaderConfig().  During AppConfig
 * startup, these parameters will are automatically added to the list of registered
 * ConfigPoints.  Values for these points need to be loaded by a preceding loader
 * or <em>forced</em> in the AppConfigBuilder.
 * 
 * Instances should not hold state because they are held in memory for the life
 * of the application.
 * 
 * @author eeverman
 */
public interface Loader {
	Map<ConfigPoint<?>, Object> load(LoaderState state) throws FatalException;
	
	/**
	 * A group of ConfigPoints used to control the loader's behavior, such
	 * the location of a properties file to load from.
	 * 
	 * @return 
	 */
	Class<? extends ConfigPointGroup> getLoaderConfig();
}
