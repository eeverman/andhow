package org.yarnandtail.andhow.load;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.property.StrProp;

/**
 * Reads from a Java .property file on the classpath, following standard
 * java conventions for the structure of those file.
 * 
 * The default location is /andhow.properties<br/>
 * It is NOT considered an error if the default properties file is not present.
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
 * The PropFileLoader is unable to detect duplicate properties (i.e., the same
 * key value appearing more than once in a prop file). Instead of aborting the
 * application startup with an error, only the last of the property values in
 * the file is assigned. This is a basic limitation of the JVM Properties class,
 * which silently ignores multiple entries, each value overwriting the last.
 *
 * @author eeverman
 */
public class AndHowPropertyFileLoader extends PropertyFileOnClasspathLoader {

	public static final String DEFAULT_PROP_FILE = "/andhow.properties";
	
	public AndHowPropertyFileLoader(StrProp classpathOfPropertyFile) {
		super(classpathOfPropertyFile);
	}

	public AndHowPropertyFileLoader(String classpathOfPropertyFile) {
		super(classpathOfPropertyFile);
	}

	public AndHowPropertyFileLoader() {
		super(DEFAULT_PROP_FILE);
	}

	/**
	 * Override to not consider it an error if the property file is not found.
	 *
	 * @param appConfigDef
	 * @param existingValues
	 * @param path
	 * @return
	 */
	@Override
	public LoaderValues load(StaticPropertyConfiguration appConfigDef,
			ValidatedValuesWithContext existingValues, String path) {

		try (InputStream inS = AndHowPropertyFileLoader.class.getResourceAsStream(path)) {

			if (inS != null) {
				
				return loadInputStreamToProps(inS, path, appConfigDef, existingValues);
				
			} else {
				
				//If the file is not there, the inS is null (no exception thrown)
				//Just ignore bc the AndHowProperty loader always tries to load
				//a default file, which the user may or may not provide.
				return new LoaderValues(this);
				
			}

		} catch (LoaderException e) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, e.getCause(), "classpath:" + path));
		} catch (IOException ioe) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, ioe, "classpath:" + path));
		}
	}

}
