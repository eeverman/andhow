package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author ericeverman
 */
public class StdConfig implements AndHowConfiguration {
	
	//A list of hardcoded values used by the FixedValueLoader
	private final List<PropertyValue> _fixedVals = new ArrayList();
	
	//A list of command line arguments
	private final List<String> _cmdLineArgs = new ArrayList();
	
	//These two are mutually exclusive
	private String propertyFileClassspathStr;
	private StrProp propertyFileClassspathProp;
	
	/**
	 * Sets a fixed, non-configurable value for a Property.
	 * 
	 * Property values set in this way use the FixedValueLoader to load values
	 * prior to any other loader.  Since the first loaded value for a Property
	 * 'wins', this effectively fixes the value and makes it non-configurable.
	 *
	 * @param <T> The type of Property and value
	 * @param property The property to set a value for
	 * @param value The value to set.
	 * @return
	 */
	public <T> StdConfig addFixedValue(Property<T> property, T value) {

		
		if (property == null) {
			throw new IllegalArgumentException("The property cannot be null");
		}
		
		for (PropertyValue pv : _fixedVals) {
			if (property.equals(pv.getProperty())) {
				throw new IllegalArgumentException("A fixed value for this property has been assigned twice.");
			}
		}
		
		PropertyValue pv = new PropertyValue(property, value);
		_fixedVals.add(pv);

		return this;
	}
	

	/**
	 * Adds the command line arguments, keeping any previously added.
	 *
	 * @param commandLineArgs
	 * @return
	 */
	public StdConfig addCmdLineArgs(String[] commandLineArgs) {

		if (commandLineArgs != null && commandLineArgs.length > 0) {
			_cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
		}

		return this;
	}

	/**
	 * Adds a command line argument in key=value form.
	 *
	 * If the value is null, only the key is added (ie its a flag).
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public StdConfig addCmdLineArg(String key, String value) {

		if (key == null)
			throw new RuntimeException("The key cannot be null");

		if (value != null) {
			_cmdLineArgs.add(key + StringArgumentLoader.KVP_DELIMITER + value);
		} else {
			_cmdLineArgs.add(key);
		}

		return this;
	}
	
	/**
	 * Sets the classpath path to a properties file for the AndHowPropertyFileLoader to load.
	 * 
	 * If no path is specified via either a String or StrProp, the path
	 * '/andhow.properties' is used.<br/>
	 * 
	 * Paths should start with a forward slash and have packages delimited by
	 * forward slashes.  If the file name contains a dot, the path <em>must</em>
	 * start with a forward slash.
	 * 
	 * @param propertyFileClassspathStr
	 * @return 
	 */
	public StdConfig setPropertyFileClasspath(String propertyFileClassspathStr) {
		
		propertyFileClassspathStr = TextUtil.trimToNull(propertyFileClassspathStr);
		
		if (propertyFileClassspathStr != null && propertyFileClassspathProp != null) {
			throw new IllegalArgumentException("The property file classpath cannot " +
					"be specified as both a String and StrProp");
		}
		
		if (propertyFileClassspathStr != null && ! propertyFileClassspathStr.startsWith("/") &&
				(propertyFileClassspathStr.endsWith(".properties") || propertyFileClassspathStr.endsWith(".xml"))) {
			
			throw new IllegalArgumentException("The path to the property file on " +
					"the classpath should start with a '/' if the filename contains a dot.");
		}
		this.propertyFileClassspathStr = propertyFileClassspathStr;
		
		return this;
	}
	

	/**
	 * Sets the classpath path via a StrProp (a Property of String type) to a
	 * properties file for the AndHowPropertyFileLoader to load.
	 * 
	 * If no path is specified via either a String or StrProp, the path
	 * '/andhow.properties' is used.<br/>
	 * 
	 * Paths should start with a forward slash and have packages delimited by
	 * forward slashes.  If the file name contains a dot, the path <em>must</em>
	 * start with a forward slash.  Thus, it is good practice to add a validation
	 * rule to the StrProp used here to ensure it <code>mustStartWith("/")</code>.
	 * 
	 * @param propertyFileClassspathProp
	 * @return 
	 */
	public StdConfig setPropertyFileClasspath(StrProp propertyFileClassspathProp) {
		
		if (propertyFileClassspathStr != null && propertyFileClassspathProp != null) {
			throw new IllegalArgumentException("The property file classpath cannot " +
					"be specified as both a String and StrProp");
		}

		this.propertyFileClassspathProp = propertyFileClassspathProp;
		
		return this;
	}


	@Override
	public List<Loader> buildLoaders() {
		List<Loader> loaders = new ArrayList();
		loaders.add(new FixedValueLoader(_fixedVals));
		loaders.add(new CommandLineArgumentLoader(_cmdLineArgs));
		loaders.add(new SystemPropertyLoader());
		loaders.add(new JndiLoader(false));
		loaders.add(new EnviromentVariableLoader());
		
		//Add Prop on Filesys loader here
		
		if (propertyFileClassspathStr != null) {
			loaders.add(new AndHowPropertyFileLoader(propertyFileClassspathStr));
		} else if (propertyFileClassspathProp != null) {
			loaders.add(new AndHowPropertyFileLoader(propertyFileClassspathProp));
		} else {
			loaders.add(new AndHowPropertyFileLoader());
		}
		
		return loaders;
	}

}
