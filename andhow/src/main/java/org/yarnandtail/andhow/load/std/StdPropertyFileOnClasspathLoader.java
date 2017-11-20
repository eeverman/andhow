package org.yarnandtail.andhow.load.std;

import org.yarnandtail.andhow.api.ValidatedValuesWithContext;
import org.yarnandtail.andhow.load.PropertyFileOnClasspathLoader;

/**
 * Same functionality as the PropertyFileOnClasspathLoader, but with a default
 * path and a separate name to track it separately as a framework provided standard loader.
 * 
 * Reads from a Java .property file on the classpath, following standard
 * java conventions for the structure of those file.
 * 
 * The default location is /andhow.properties<br/>
 *
 * This loader finds the properties file via either a String or StrProperty
 * specified in the constructor.  If the StrProperty is used, an earlier
 * an earlier loading loader must have found a value for that property if it
 * is to be used. The default location is /andhow.properties
 *
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property. For strings, this is the QuotedStringTrimmer
 * by default, which will preserve whitespace if wrapped in double quotes.
 *
 * Properties File Loaders use the java.util.Properties class to read
 * properties, so several behaviors are determined by that class.
 *
 * This loader considers it a problem to find unrecognized properties in a
 * properties file and will throw a RuntimeException if that happens.
 *
 * Property File Loaders are unable to detect duplicate properties (i.e., the same
 * key value appearing more than once in a prop file). Instead of aborting the
 * application startup with an error, only the last of the property values in
 * the file is assigned. This is a basic limitation of the JVM Properties class,
 * which silently ignores multiple entries, each value overwriting the last.
 *
 * @author eeverman
 */
public class StdPropertyFileOnClasspathLoader extends PropertyFileOnClasspathLoader {

	public static final String DEFAULT_PROP_FILE = "/andhow.properties";
	
	public StdPropertyFileOnClasspathLoader() {
		missingFileAProblem = false;
	}

	@Override
	protected String getEffectivePath(ValidatedValuesWithContext existingValues) {
		String path = super.getEffectivePath(existingValues);
		if (path != null) {
			return path;
		} else {
			return DEFAULT_PROP_FILE;
		}
	}
}
