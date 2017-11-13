package org.yarnandtail.andhow.load;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.property.StrProp;

/**
 * Reads from a Java .property file on the classpath, following standard
 * java conventions for the structure of those file.
 *
 * This loader finds the properties file via either a String or StrProperty
 * specified in the constructor.  If the StrProperty is used, an earlier
 * an earlier loading loader must have found a value for that property if it
 * is to be used.
 *
 * It is considered an error if its configured classpathProp does not point to a
 * valid properties file. It is not considered an error if the classpathProp
 * property has not been assigned a value or a null string or StrProp are assigned.
 * Assigning or configuring a null classpath effectively turns off this loader.
 *
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property. For strings, this is the QuotedStringTrimmer
 * by default, which will preserve whitespace if wrapped in double quotes.
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
public class PropertyFileOnClasspathLoader extends PropertyFileBaseLoader {

	/**
	 * Property containing classpath of a property file. XOR w/ classpathStr
	 */
	final StrProp classpathProp;

	/**
	 * String containing classpath of a property file. XOR w/ classpathProp
	 */
	final String classpathStr;

	String specificLoadDescription = null;

	public PropertyFileOnClasspathLoader(StrProp classpathOfPropertyFile) {
		classpathProp = classpathOfPropertyFile;
		classpathStr = null;
	}

	public PropertyFileOnClasspathLoader(String classpathOfPropertyFile) {
		classpathProp = null;
		classpathStr = classpathOfPropertyFile;
	}

	public PropertyFileOnClasspathLoader() {
		classpathProp = null;
		classpathStr = null;
	}

	@Override
	public LoaderValues load(StaticPropertyConfiguration appConfigDef, ValidatedValuesWithContext existingValues) {

		String path = getEffectiveClasspath(existingValues);

		if (path != null) {

			specificLoadDescription = "file on classpath at: " + path;

			LoaderValues vals = load(appConfigDef, existingValues, path);
			return vals;

		} else {
			//The classpathProp is not specified, so just ignore

			specificLoadDescription = "unpsecified file on classpath";
			return new LoaderValues(this);
		}
	}

	protected String getEffectiveClasspath(ValidatedValuesWithContext existingValues) {
		if (classpathStr != null) {
			return classpathStr;
		} else if (classpathProp != null) {
			return existingValues.getValue(classpathProp);
		} else {
			return null;
		}
	}

	/**
	 * Load from a non-null classpathProp path.
	 *
	 * @param appConfigDef
	 * @param existingValues
	 * @param path
	 * @return
	 */
	public LoaderValues load(StaticPropertyConfiguration appConfigDef,
			ValidatedValuesWithContext existingValues, String path) {

		try (InputStream inS = PropertyFileOnClasspathLoader.class.getResourceAsStream(path)) {

			if (inS != null) {
				
				return loadInputStreamToProps(inS, path, appConfigDef, existingValues);

			} else {
				
				//If the file is not there, the inS is null (no exception thrown)
				//Handle by adding a loader error
				return new LoaderValues(this, new LoaderProblem.SourceNotFoundLoaderProblem(this, "Expected file at classpath:" + path));
			
			}

		} catch (LoaderException e) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, e.getCause(), "classpath:" + path));
		} catch (IOException ioe) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, ioe, "classpath:" + path));
		}
	}

	@Override
	public List<Property> getInstanceConfig() {

		if (classpathProp != null) {
			ArrayList<Property> list = new ArrayList();
			list.add(classpathProp);
			return list;
		} else {
			return Collections.emptyList();
		}

	}

	@Override
	public String getSpecificLoadDescription() {

		if (specificLoadDescription != null) {
			return specificLoadDescription;
		} else {
			return "file on classpath at: " + classpathProp.getValue();
		}
	}

}
