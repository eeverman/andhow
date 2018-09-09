package org.yarnandtail.andhow.load.std;

import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.KeyValuePairLoader;

/**
 * Reads an array of Strings containing key value pairs in the form <em>key=value</em>,
 * and loads the value for any key that matches a Property.  This is normally
 * used to read in command line arguments.
 * <h3>Position in Standard Loading Order, first to last</h3>
 * <ul>
 * <li>StdFixedValueLoader
 * <li><b>StdMainStringArgsLoader &lt;-- This loader</b>
 * <li>StdSysPropLoader
 * <li>StdEnvVarLoader
 * <li>StdJndiLoader
 * <li>StdPropFileOnFilesystemLoader
 * <li>StdPropFileOnClasspathLoader
 * </ul>
 * <em>Property value loading is based on a 'first win' strategy, so the first
 * loader to find a value for a property sets the value.</em>
 * <h3>Typical Use Case</h3>
 * An executable jar or desktop application accepts command line arguments at
 * startup to configure the application.  This loader loads the
 * {@code String[] args} passed to the {@code main(String[] args)}, which Java
 * passes from the command line.  This is the most common usage, however, your
 * application may retrieve an array of {@code String}'s from anywhere to pass
 * to AndHow at startup.
 * <h3>Basic Behaviors</h3>
 * <ul>
 * <li><b>Pre-trims String values: Yes</b>
 * <li><b>Complains about unrecognized properties: No</b>
 * <li><b>Default behavior:  None</b> - This loader is only active if command line arguments are passed in as shown below
 * </ul>
 * <h3>Loader Details and Configuration</h3>
 * AndHow is not 'there' to somehow intercept command line arguments -
 * Your application code will need to do that and pass those arguments to
 * AndHow, like this:
 * <br>
 * <pre>
 * import org.yarnandtail.andhow.*;
 * 
 * public class MyAppClass {
 *   public static void main(String[] args) {
 *     AndHow.findConfig()
 *       .setCmdLineArgs(args)
 *       .build();
 *   }
 * }
 * </pre>
 * Passing values from the command line looks like this:
 * <pre>{@code java -jar MyJarName.jar full.name.of.MY_PROPERTY=someValue}</pre>
 * <a href="https://sites.google.com/view/andhow/usage-examples/main-startup-example">
 * Here is a complete example of using command line arguments</a>.
 * 
 * <h3>This is a Standard Loader</h3>
 * Like all {@code StandardLoader}'s, this loader is intended to be auto-created
 * by AndHow.  The set of standard loaders and their order can bet set
 * via the {@code AndHowConfiguration.setStandardLoaders()} methods.
 * Other loaders which don't implement the {@code StandardLoader} interface can
 * be inserted into the load order via the
 * {@code AndHowConfiguration.insertLoaderBefore/After()}.
 * 
 * @author ericeverman
 */
public class StdMainStringArgsLoader extends KeyValuePairLoader 
		implements StandardLoader {

	/**
	 * There is no reason to use the constructor in production application code
	 * because AndHow creates a single instance on demand at runtime.
	 */
	public StdMainStringArgsLoader() {
		unknownPropertyAProblem = false;
	}

}
