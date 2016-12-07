package yarnandtail.andhow;

import java.util.List;
import yarnandtail.andhow.appconfig.AppConfigDefinition;

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
	LoaderValues load(AppConfigDefinition appConfigDef, List<String> cmdLineArgs,
			AppConfigStructuredValues existingValues, 
			List<LoaderException> loaderExceptions) throws FatalException;
	
	/**
	 * A group of ConfigPoints used to control the loader's behavior, such
	 * the location of a properties file to load from.
	 * 
	 * @return 
	 */
	Class<? extends ConfigPointGroup> getLoaderConfig();
	
	/**
	 * For this particular load, where was info loaded from?
	 * 
	 * This may vary from run to run based on config params.  For instance,
	 * The PropFileLoader will look multiple places to find a properties file.
	 * This method should return the actual location it was found.  Assume the
	 * text is prefixed with "This loader loaded from: ".
	 * 
	 * An example would be "/home/user/config/my-props.properties"
	 * 
	 * No ending punctuation should be used.
	 * 
	 * @return 
	 */
	String getSpecificLoadDescription();
}
