package org.yarnandtail.andhow.load;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.property.StrProp;

/**
 * Reads from a Java .property file on the classpath, following standard java
 * conventions for the structure of those file. The default location is
 * /andhow.properties
 *
 *
 * It is NOT considered an error if the default properties file is not present.
 *
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property. This loader considers it a problem to
 * find unrecognized properties in a properties file and will throw a
 * RuntimeException if that happens.
 *
 * Properties File Loaders use the java.util.Properties class to read
 * properties, so several behaviors are determined by that class.
 *
 * In rare cases, whitespace handling of the JVM Properties file parser may be
 * an issue. The property value is generally terminated by the end of the line.
 * Whitespace following the property value is not ignored, and is treated as
 * part of the property value. This is not a problem in most cases because by
 * default, properties have Trimmers that remove whitespace. Other Trimmer
 * implementations can be assigned to properties, however, so be aware of the
 * implementations if your are using non-default Trimmers.
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

	public AndHowPropertyFileLoader() {
		super(CONFIG.PROP_FILE_PATH);
	}

	/**
	 * Load from a non-null classpath path.
	 *
	 * @param appConfigDef
	 * @param existingValues
	 * @param path
	 * @return
	 */
	public LoaderValues load(StaticPropertyConfiguration appConfigDef,
			PropertyValuesWithContext existingValues, String path) {

		try (InputStream inS = AndHowPropertyFileLoader.class.getResourceAsStream(path)) {

			if (inS == null) {
				//If the file is not there, the inS is null (no exception thrown)
				return new LoaderValues(this);

				//TODO:  It would be nice to add a warning or something in this case
			}

			return loadInputStreamToProps(inS, path, appConfigDef, existingValues);

		} catch (LoaderException e) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, e.getCause(), "classpath:" + path));
		} catch (IOException ioe) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, ioe, "classpath:" + path));
		}
	}

	@Override
	public List<Property> getInstanceConfig() {
		return Collections.emptyList();
	}

	@Override
	public Class<?> getClassConfig() {
		return CONFIG.class;
	}

	@GroupInfo(
			name = "AndHow Property File Loader Configuration",
			desc = "By default, AndHow will load properties found in a properties "
			+ "file on the classpath using the AndHowPropertyFileLoader loader. "
			+ "the option(s) here configure that behaviour.")
	public static interface CONFIG {

		StrProp PROP_FILE_PATH = StrProp.builder()
				.desc("The full classpath and file name of a property file to load properties from")
				.helpText("The full classpath should start with a slash and include the file name and extension, "
						+ "eg '/bigbiz/myproject/config.properties").defaultValue("/andhow.properties").mustStartWith("/").build();

	}

}
