package org.yarnandtail.andhow.load.std;

import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.api.ValidatedValuesWithContext;
import org.yarnandtail.andhow.load.PropFileOnClasspathLoader;

/**
 * Parses and loads Properties from a Java {@code .property} file on the
 * <em>classpath</em>. By default, this loader will look for a file named
 * {@code andhow.properties} at the root of the classpath.
 * 
 * <h4>Position in Standard Loading Order, first to last</h4>
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
 * <h4>Typical Use Case</h4>
 * A service application might load the majority of its configuration from
 * system properties or environmental variables, however, some sane default
 * configuration values can be bundled with the application.
 * By default, AndHow will discover and load a file named {@code andhow.properties}
 * at the root of the classpath via the {@code StdPropFileOnClasspathLoader}.
 * <h4>Basic Behaviors</h4>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: Yes</b>
 * <li><b>Complains if the .properties file is missing: No</b>
 * <li><b>Default behavior:  Attempts to read {@code andhow.properties} from the root of the classpath</b>
 * </ul>
 * <h4>Other Loader Details</h4>
 * This loader reads properties files using the {@code java.util.Properties},
 * which silently ignores duplicate property entries
 * (i.e., the same key appearing multiple times).  When there are duplicate 
 * property keys in a properties file, only the last assigned value is used.
 * Full details on how Java parses properties files can be found in the
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-">properties file specification</a>.
 * <p>
 * Configuring the name or classpath of the properties file can be used to
 * enable different configuration profiles based on the environment.
 * For instance, a system property could specify that {@code /test.properties}
 * be used on a test server and {@code /production.properties} on a production
 * server.  An example of configuring the property file path:
 * <pre>
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
 * </pre>
 *  The code above adds the property {@code MY_CLASSPATH}
 * (the name is arbitrary) which is used to configure the 
 * {@code StdPropFileOnClasspathLoader} with a custom property file location.
 * When AndHow initializes, the {@code StdPropFileOnClasspathLoader} checks to
 * see if a value has been loaded for {@code MY_CLASSPATH} by any prior loader.
 * If a value is present, the loader tries to load from the configured classpath.
 * If no value is configured, the default classpath is assumed.
 * 
 * <h4>This is a Standard Loader</h4>
 * Like all {@code StandardLoader}'s, this loader is intended to be auto-created
 * by AndHow.  The set of standard loaders and their order can bet set
 * via the {@code AndHowConfiguration.setStandardLoaders()}
 * methods, which take a list of {@code Class<StandardLoader>}'s.
 * Other loaders which don't implement the {@code StandardLoader} interface can
 * be added to the load order via the
 * {@code AndHowConfiguration.insertLoaderBefore/After()}.
 *
 * @author eeverman
 */
public class StdPropFileOnClasspathLoader extends PropFileOnClasspathLoader 
		implements StandardLoader {

	public static final String DEFAULT_PROP_FILE = "/andhow.properties";
	
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
