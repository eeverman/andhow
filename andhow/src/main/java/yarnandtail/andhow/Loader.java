package yarnandtail.andhow;

import java.util.List;
import yarnandtail.andhow.internal.RuntimeDefinition;

/**
 * Each instance is responsible for loading values from a particular type of source.
 * 
 * Implementations may define a set of Properties used to control the behavior
 * of the loader, which are returned from getLoaderConfig().  During AndHow
 * startup, these parameters will are automatically added to the list of registered
 * Properties.  Values for these properties need to be loaded by a preceding loader
 * or <em>forced</em> in the AndHowBuilder.
 * 
 * Instances should not hold state because they are held in memory for the life
 * of the application.
 * 
 * @author eeverman
 */
public interface Loader {
	LoaderValues load(RuntimeDefinition runtimeDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues);
	
	/**
	 * A group of Properties used to control the loader's behavior, such
	 * the location of a properties file to load from.
	 * 
	 * @return 
	 */
	Class<? extends PropertyGroup> getLoaderConfig();
	
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
