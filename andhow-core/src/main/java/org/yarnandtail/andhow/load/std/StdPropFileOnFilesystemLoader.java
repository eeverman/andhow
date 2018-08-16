package org.yarnandtail.andhow.load.std;

import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.PropFileOnFilesystemLoader;

/**
 * Parses and loads Properties from a Java {@code .property} file on the
 * <em>file system</em>.  Since file systems vary, there is no default filepath
 * that AndHow attempts to load from. 
 * <h4>Position in Standard Loading Order, first to last</h4>
 * <ul>
 * <li>StdFixedValueLoader
 * <li>StdMainStringArgsLoader
 * <li>StdSysPropLoader
 * <li>StdEnvVarLoader
 * <li>StdJndiLoader
 * <li><b>StdPropFileOnFilesystemLoader &lt;-- This loader</b>
 * <li>StdPropFileOnClasspathLoader
 * </ul>
 * <em>Property value loading is based on a 'first win' strategy, so the first
 * loader to find a value for a property sets the value.</em>
 * <h4>Typical Use Case</h4>
 *  A service application might load minimal configuration from system
 * properties or environmental variables, loading the bulk of its configuration
 * from a properties file on the file system.  The properties file is not part
 * of the application, so it survives redeployments.  A single environmental or
 * system property could then be used to specify the properties file.
 * <h4>Basic Behaviors</h4>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: Yes</b>
 * <li><b>Complains if the .properties file is missing: Yes (if a file path is configured)</b>
 * <li><b>Default behavior:  None - If not explicitly configured, no default loading is attempted.</b>
 * </ul>
 * <h4>Loader Details and Configuration</h4>
 * This loader reads properties files using the {@code java.util.Properties},
 * which silently ignores duplicate property entries
 * (i.e., the same key appearing multiple times).  When there are duplicate 
 * property keys in a properties file, only the last assigned value is used.
 * Full details on how Java parses properties files can be found in the
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-">properties file specification</a>.
 * <p>
 * This loader is only active if it is configured as shown below:
 * <br>
 * <pre>
 * import org.yarnandtail.andhow.*;
 * import org.yarnandtail.andhow.property.StrProp;
 * 
 * public class UsePropertyFileOnFilesystem implements AndHowInit {
 *   public static final StrProp MY_FILEPATH = StrProp.builder()
 *     .desc("Path to a properties file on the file system. "
 *       + "If a path is configured, startup will FAIL if the file is missing.").build();
 * 
 *   {@literal @}Override
 *   public AndHowConfiguration getConfiguration() {
 *     return  StdConfig.instance()
 *       .setFilesystemPropFilePath(MY_FILEPATH);
 *   }
 * }
 * </pre>
 * <br>
 * The code above adds the property {@code MY_FILEPATH}
 * (the name is arbitrary) which is used to configure the 
 * {@code StdPropFileOnFilesystemLoader} with a custom property file location.
 * When AndHow initializes, the {@code StdPropFileOnFilesystemLoader} checks to
 * see if a value has been loaded for {@code MY_FILEPATH} by any prior loader.
 * If a value is present, the loader tries to load from the configured file
 * system path.  If no value is configured, this loader is skipped.
 * 
 * <h4>This is a Standard Loader</h4>
 * Like all {@code StandardLoader}'s, this loader is intended to be auto-created
 * by AndHow.  The set of standard loaders and their order can bet set
 * via the {@code AndHowConfiguration.setStandardLoaders()} methods.
 * Other loaders which don't implement the {@code StandardLoader} interface can
 * be inserted into the load order via the
 * {@code AndHowConfiguration.insertLoaderBefore/After()}.
 *
 * @author eeverman
 */
public class StdPropFileOnFilesystemLoader extends PropFileOnFilesystemLoader
		implements StandardLoader {

	/**
	 * There is no reason to use the constructor in production application code
	 * because AndHow creates a single instance on demand at runtime.
	 */
	public StdPropFileOnFilesystemLoader() {
	}

}
