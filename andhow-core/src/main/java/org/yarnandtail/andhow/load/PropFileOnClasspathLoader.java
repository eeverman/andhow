package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.io.IOException;
import java.io.InputStream;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;

/**
 * Reads from a Java .properties file on the classpath, following standard java
 * conventions for the structure of those key:value pair files.
 *
 * This loader finds the properties file via either a String or StrProperty
 * specified in the constructor. If the StrProperty is used, an earlier an
 * earlier loading loader must have found a value for that property if it is to
 * be used.
 *
 * Assigning or configuring a null classpath effectively turns off this loader.
 *
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property. For strings, this is the
 * QuotedStringTrimmer by default, which will preserve whitespace if wrapped in
 * double quotes.
 *
 * By default, this loader considers it a problem not find a configured file or
 * to find unrecognized properties in a properties file and will throw a
 * RuntimeException if that happens.
 *
 * The PropFileLoader is unable to detect duplicate properties (i.e., the same
 * key value appearing more than once in a prop file). Instead of aborting the
 * application startup with an error, only the last of the property values in
 * the file is assigned. This is a basic limitation of the JVM Properties class,
 * which silently ignores multiple entries, each value overwriting the last.
 *
 * @author eeverman
 */
public class PropFileOnClasspathLoader extends PropFileBaseLoader {

	String specificLoadDescription = null;

	public PropFileOnClasspathLoader() {
		/* empty for easy construction */ }

	@Override
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef, ValidatedValuesWithContext existingValues) {

		String path = getEffectivePath(existingValues);

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

	/**
	 * Load from a non-null classpathProp path.
	 *
	 * @param appConfigDef
	 * @param existingValues
	 * @param path
	 * @return
	 */
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef,
			ValidatedValuesWithContext existingValues, String path) {

		try (InputStream inS = PropFileOnClasspathLoader.class.getResourceAsStream(path)) {

			if (inS != null) {

				return loadInputStreamToProps(inS, path, appConfigDef, existingValues);

			} else {

				//If the file is not there, the inS is null (no exception thrown)
				if (isMissingFileAProblem()) {
					return new LoaderValues(this, new LoaderProblem.SourceNotFoundLoaderProblem(this, "Expected file at classpath:" + path));
				} else {
					return new LoaderValues(this);
				}
			}

		} catch (LoaderException e) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, e.getCause(), "classpath:" + path));
		} catch (IOException ioe) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, ioe, "classpath:" + path));
		}
	}

	@Override
	public String getSpecificLoadDescription() {

		if (specificLoadDescription != null) {
			return specificLoadDescription;
		} else {
			String path = this.getEffectivePath(null);
			if (path != null) {
				return "file on classpath at: " + path;
			} else {
				return "unconfigured classpath";
			}
		}
	}

}
