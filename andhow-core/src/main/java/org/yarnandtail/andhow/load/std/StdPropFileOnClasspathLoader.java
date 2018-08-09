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
 * A typical service application pulls in the majority of its configuration from
 * system properties, environmental variables and JNDI.  The application has some
 * configuration settings that need sane defaults, so an {@code andhow.properties}
 * file is included with the application at the root of the classpath.
 * With no additional configuration, AndHow will discover the file and load its
 * property values via the {@code StdPropFileOnClasspathLoader}.
 * <h4>Basic Behaviors</h4>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: Yes</b>
 * <li><b>Complains if the .properties file is missing: No</b>
 * </ul>
 * <h4>Other Loader Details</h4>
 * This loader and all properties file based loaders use
 * {@code java.util.Properties} to parse properties files, so several behaviors
 * are determined by that class.
 * First, {@code Properties} silently ignores duplicate property entries
 * (i.e., the same key value appearing multiple times). In this situation,
 * only the last of the property values in the file is used.
 * Second, key and value delimiters, line endings and line continuation are 
 * determined according to the
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-">properties file specification</a>.
 * <p>
 * If a property file location other than the default ({@code /andhow.properties})
 * is needed, a new location can be configured like this:
 * <pre>
 * import org.yarnandtail.andhow.*;
 * import org.yarnandtail.andhow.property.StrProp;
 * 
 * public class UsePropertyFileOnClasspath implements AndHowInit {
 *   public static final StrProp MY_PROP_FILE_ON_CLASSPATH = StrProp.builder()
 *	   .desc("Path to a properties file on the classpath. "
 *       + "If the file is not present, it is not considered an error.").build();
 * 
 *   {@literal @}Override
 *   public AndHowConfiguration getConfiguration() {
 *     return  StdConfig.instance()
 *       .setClasspathPropFilePath(MY_PROP_FILE_ON_CLASSPATH);
 *   }
 * }
 * </pre>
 * The code above adds a String Property named {@code MY_PROP_FILE_ON_CLASSPATH}
 * (the name is arbitrary)
 * which is used to tell {@code AndHow} where to find a properties file
 * on the classpath.  When {@code AndHow} initializes, the
 * {@code StdPropFileOnClasspathLoader} checks to see if a value has been loaded
 * for {@code MY_PROP_FILE_ON_CLASSPATH} by a prior loader.  If a value is present,
 * the loader tries to load from the configured classpath.  If no value is
 * configured, a default classpath of {@code /andhow.properties} is assumed.
 * <br>
 * Adding a {@code Property} to enable a custom classpath properties file can be useful
 * for testing or common deployment profiles.  For instance, a system property
 * could specify that {@code /test.properties} be used during testing,
 * {@code /qa_mode.properties} on a QA server and {@code /production.properties} on
 * a production server.
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
