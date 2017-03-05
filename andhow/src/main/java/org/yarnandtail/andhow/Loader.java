package org.yarnandtail.andhow;

import java.util.List;

/**
 * Each instance is responsible for loading values from a particular type of source.
 * 
 * Implementations may define a set of Properties used to control the behavior
 of the loader, which are returned from getClassConfig().  During AndHow
 startup, these parameters will are automatically added to the list of registered
 Properties.  Values for these properties need to be loaded by a preceding loader
 or <em>forced</em> in the AndHowBuilder.
 * 
 * Instances should not hold state because they are held in memory for the life
 * of the application.
 * 
 * @author eeverman
 */
public interface Loader {
	
	/**
	 * Builds up a list of LoaderValues by loading property values from a configuration
	 * source.
	 * 
	 * @param runtimeDef
	 * @param cmdLineArgs All loaders receive the command line args even though they only apply to the cmd line loader
	 * @param existingValues
	 * @return 
	 */
	LoaderValues load(ConstructionDefinition runtimeDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues);
	
	/**
	 * A group of Properties used to globally configure all instance of a type
	 * of loader.
	 *
	 * For loaders that are intended to be single instance (i.e. a JNDI loader)
	 * or loaders that can be multi-instance (PropertyFile loaders) but need to
	 * share some common configuration can return a PropertyGroup from this
	 * method that is their global configuration interface.
	 *
	 * For configuration properties that should vary between instances of the
	 * same loader class, accept them in the Loader constructor. (See
	 * getInstanceConfig)
	 *
	 * The PropertyGroup returned from this method will be included in sample
	 * config files. It is up to the Loader class to read from those configured
	 * properties if the user configures them.
	 *
	 * @return
	 */
	Class<? extends PropertyGroup> getClassConfig();
	
	/**
	 * A list of properties that the user has specified as being configuration
	 * properties for this Loader.
	 * 
	 * Typically these Properties have been specified in the Loader constructor
	 * or builder.  An example usage would be passing in a Property that specifies
	 * the location of a properties file to read from.  This is the preferred way
	 * to configure a loader because its clear to the user and it allows each
	 * instance of a loader to be configured differently, e.g., so PropertyFile
	 * loaders that load from different locations.
	 * 
	 * Any Properties returned from this method must have been registered for
	 * configuration.
	 * 
	 * @return Never null.  Either an empty list or populated.
	 */
	List<Property> getInstanceConfig();
	
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
	
	/**
	 * If true, values for string properties are trimmed by the Trimmer of
	 * the property they are associated with.
	 * 
	 * If false, no trimming is done.  Non-String type property values (like
	 * Integer or Date type values) are not affected by this.
	 * 
	 * Loaders that read from text files need to trim values to separate white
	 * space and carriage returns from the value.  Loaders that read from more 
	 * tightly defined sources, like reading properties from 
	 * java.lang.System.getProperties(), can assume that trimming has
	 * already happened.
	 * 
	 * @return 
	 */
	boolean isTrimmingRequiredForStringValues();
	
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
	boolean isUnrecognizedPropertyNamesConsideredAProblem();
	
	/**
	 * Returns a ConfigSamplePrinter, which can be used to print a configuration
	 * sample for this Loader.
	 * 
	 * This method may return null if there is no sample printer for this loader.
	 * 
	 * @return 
	 */
	SamplePrinter getConfigSamplePrinter();
}
