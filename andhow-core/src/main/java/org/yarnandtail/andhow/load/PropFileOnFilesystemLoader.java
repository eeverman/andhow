package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;
import java.io.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Reads from a Java .property file from the filesystem, following standard java
 * conventions for the structure of those file.
 *
 * This loader finds the properties file to load properties from
 * based on a file path property passed in its constructor. If this loader is
 * to read that property, an earlier loader must have loaded a value for it.
 * It is not considered an error if the file path property has not been assigned
 * a value.
 *
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property.
 *
 * Properties File Loaders use the java.util.Properties class to read
 * properties, so several behaviors are determined by that class.
 *
 * By default, this loader considers it a problem not find a configured file or
 * to find unrecognized properties in a properties file and will throw a
 * RuntimeException if that happens.
 *
 * Property File Loaders are unable to detect duplicate properties (i.e., the same
 * key value appearing more than once in a prop file). Instead of aborting the
 * application startup with an error, only the last of the property values in
 * the file is assigned. This is a basic limitation of the JVM Properties class,
 * which silently ignores multiple entries, each value overwriting the last.
 *
 * @author eeverman
 */
public class PropFileOnFilesystemLoader extends PropFileBaseLoader {

	String specificLoadDescription = null;

	public PropFileOnFilesystemLoader() {
		/* empty for easy construction */ }


	@Override
	public LoaderValues load(PropertyConfigurationInternal appConfigDef,
			LoaderEnvironment environment, ValidatedValuesWithContext existingValues) {

		String path = getEffectivePath(existingValues);

		if (path != null) {

			specificLoadDescription = TextUtil.format("file on the file system at path : {} ({})",
			path, getAbsPath(path));

			LoaderValues vals = load(appConfigDef, existingValues, path);
			return vals;

		} else {
			//The path is not specified, so just ignore

			specificLoadDescription = "unpsecified file in the filesystem";
			return new LoaderValues(this);
		}
	}

	public LoaderValues load(PropertyConfigurationInternal appConfigDef,
			ValidatedValuesWithContext existingValues, String path) {

		if (path != null) {

			try {
				File propFile = new File(path);

				try (FileInputStream inS = new FileInputStream(propFile)) {

					return loadInputStreamToProps(inS, path, appConfigDef, existingValues);

				} catch (FileNotFoundException e) {

					if (isMissingFileAProblem()) {
						return new LoaderValues(this, new LoaderProblem.SourceNotFoundLoaderProblem(this, "Expected file on filesystem:" + path));
					} else {
						return new LoaderValues(this);
					}
				}

			} catch (LoaderException e) {
				return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, e.getCause(), "filesystem:" + path));
			} catch (IOException ioe) {
				return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, ioe, "filesystem:" + path));
			}

		} else {
			//The filepath to loadJavaPropsToAndhowProps from is not specified, so just ignore it
			return new LoaderValues(this);
		}
	}

	@Override
	public String getSpecificLoadDescription() {

		if (specificLoadDescription != null) {
			return specificLoadDescription;
		} else {

			String path = this.getEffectivePath(null);
			if (path != null) {
				return TextUtil.format("file on the file system at path : {} ({})",
					path, getAbsPath(path)) ;
			} else {
				return "unconfigured classpath";
			}
		}
	}

	/**
	 * Completely safe way to convert a file system path to an absolute path.
	 * never errors or returns null.
	 * @param anything
	 * @return
	 */
	private String getAbsPath(String anything) {

		try {
			File f = new File(anything);
			return f.getAbsolutePath();
		} catch (Exception e) {
			return "[Unknown absolute path]";
		}

	}

}
