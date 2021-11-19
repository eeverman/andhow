package org.yarnandtail.andhow.load.std;

import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.api.ValidatedValuesWithContext;
import org.yarnandtail.andhow.load.PropFileOnClasspathLoader;

/**
 * Parses and loads Properties from a Java {@code .property} file on the
 * <em>classpath</em>. By default, this loader will look for a file named
 * {@code andhow.properties} at the root of the classpath.
 *
 * <h3>Position in Standard Loading Order, first to last</h3>
 * <ul>
 * <li>StdFixedValueLoader
 * <li>StdMainStringArgsLoader
 * <li>StdSysPropLoader
 * <li>StdEnvVarLoader
 * <li>StdJndiLoader
 * <li>StdPropFileOnFilesystemLoader
 * <li><b>StdPropFileOnClasspathLoader &lt;-- This loader</b>
 * </ul>
 * <em>Property value loading is based on a 'first win' strategy, so the first
 * loader to find a value for a property sets the value.</em>
 * <h3>Typical Use Case</h3>
 * A service application might load the majority of its configuration from
 * system properties or environmental variables, however, some sane default
 * configuration values can be bundled with the application.
 * By default, AndHow will discover and load a file named {@code andhow.properties}
 * at the root of the classpath via the {@code StdPropFileOnClasspathLoader}.
 * <h3>Basic Behaviors</h3>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: Yes</b>
 * <li><b>Complains if the .properties file is missing: No</b>
 * <li><b>Default behavior:  Attempts to read {@code andhow.properties} from the root of the classpath</b>
 * </ul>
 * <h3>Loader Details and Configuration</h3>
 * This loader reads properties files using the {@code java.util.Properties},
 * which silently ignores duplicate property entries
 * (i.e., the same key appearing multiple times).  When there are duplicate
 * property keys in a properties file, only the last assigned value is used.
 * Full details on how Java parses properties files can be found in the
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-">properties file specification</a>.
 * <p>
 * Since resources on the test classpath override those on the main classpath,
 * its easy to have separate configurations for production and testing simply
 * by having two {@code andhow.properties} files, one on the main classpath
 * and one on test.  There are many other options with classpath properties files.
 * <p>
 * To change the name of the properties file used for testing, a class like the
 * example below can be added to your test classpath:
 * <pre>{@code
 * import org.yarnandtail.andhow.*;
 * import org.yarnandtail.andhow.property.StrProp;
 *
 * public class UsePropertyFileOnClasspath implements AndHowTestInit {
 *
 *   {@literal @}Override
 *   public AndHowConfiguration getConfiguration() {
 *     return  StdConfig.instance()
 *       .setClasspathPropFilePath("/my_test_configuration_properties_file.prop");
 *   }
 * }
 * }</pre>
 * If AndHow finds a {@code AndHowTestInit} implementation on the classpath,
 * it uses it in preference to other initiation points.
 * With this only on the test classpath, AndHow will still use the default
 * {@code andhow.properties} for production.
 * <p>
 * With slightly fancier configuration, something like {@code /staging.properties}
 * could be used in a staging environment and {@code /production.properties}
 * used in production.  In a case like this we need the classpath to be
 * configurable, not hard-coded like the example above.  To do that we need
 * a Property to configure it with, then we tell AndHow which property was
 * used.<br>
 * Here is an example of a configurable classpath:
 * <pre>{@code
 * import org.yarnandtail.andhow.*;
 * import org.yarnandtail.andhow.property.StrProp;
 *
 * public class UsePropertyFileOnClasspath implements AndHowInit {
 *   public static final StrProp MY_CLASSPATH = StrProp.builder()
 *	   .desc("Path to a properties file on the classpath. "
 *       + "If the file is not present, it is not considered an error.").build();
 *
 *   {@literal @}Override
 *   public AndHowConfiguration getConfiguration() {
 *     return  StdConfig.instance()
 *       .setClasspathPropFilePath(MY_CLASSPATH);
 *   }
 * }
 * }</pre>
 * The code above adds the property {@code MY_CLASSPATH}
 * (the name is arbitrary) which is used to configure the
 * {@code StdPropFileOnClasspathLoader} with a custom property file location.
 * When AndHow initializes, the {@code StdPropFileOnClasspathLoader} checks to
 * see if a value has been loaded for {@code MY_CLASSPATH} by any prior loader.
 * If a value is present, the loader tries to load from the configured classpath.
 * If no value is configured, the default classpath is assumed.
 * Properties file loaders are the last loaders, so configuration the classpath
 * can be done by virtually anything: environment vars, system properties,
 * command line args, JNDI...
 *
 * <h3>This is a Standard Loader</h3>
 * Like all {@code StandardLoader}'s, this loader is intended to be auto-created
 * by AndHow.  The set of standard loaders and their order can bet set
 * via the {@code AndHowConfiguration.setStandardLoaders()} methods.
 * Other loaders which don't implement the {@code StandardLoader} interface can
 * be inserted into the load order via the
 * {@code AndHowConfiguration.insertLoaderBefore/After()}.
 */
public class StdPropFileOnClasspathLoader extends PropFileOnClasspathLoader
		implements StandardLoader {

	public static final String DEFAULT_PROP_FILE = "/andhow.properties";

	/**
	 * There is no reason to use the constructor in production application code
	 * because AndHow creates a single instance on demand at runtime.
	 */
	public StdPropFileOnClasspathLoader() {
		missingFileAProblem = false;
	}

	@Override
	protected String getEffectivePath(ValidatedValuesWithContext existingValues) {
		String path = super.getEffectivePath(existingValues);
		if (path != null) {
			return path;
		} else if (pathProp != null) {
			return null;	//There is a StrProp to specify the path, but the value is null
		} else {
			return DEFAULT_PROP_FILE;
		}
	}
}
