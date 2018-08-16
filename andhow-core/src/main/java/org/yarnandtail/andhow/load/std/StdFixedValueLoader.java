package org.yarnandtail.andhow.load.std;

import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.FixedValueLoader;

/**
 * Sets values directly in code.
 * 
 * <h4>Position in Standard Loading Order, first to last</h4>
 * <ul>
 * <li><b>StdFixedValueLoader &lt;-- This loader</b>
 * <li>StdMainStringArgsLoader
 * <li>StdSysPropLoader
 * <li>StdEnvVarLoader
 * <li>StdJndiLoader
 * <li>StdPropFileOnFilesystemLoader
 * <li>StdPropFileOnClasspathLoader
 * </ul>
 * <em>Property value loading is based on a 'first win' strategy, so the first
 * loader to find a value for a property sets the value.</em>
 * <h4>Typical Use Case</h4>
 * Since this loader uses fixed values in code, it is only useful for specifying
 * configuration of your application during testing.  Configuration values can
 * be directly set in code so that your application can be tested in a specific
 * configured state.  Since this is the first loader, a property value set by
 * this load will override configuration values found by any other loader.
 * <h4>Basic Behaviors</h4>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: NA</b> - This is not possible
 * <li><b>Default behavior:  None</b> - This loader is only active if values are directly set as shown below
 * </ul>
 * <h4>Loader Details and Configuration</h4>
 * One simple way to set fixed values is shown below.  AndHow will discover this
 * class implementing the {@code AndHowInit} interface during initialization to
 * read your configuration.  That looks like this:
 * <br>
 * <pre>
 * import org.yarnandtail.andhow.*;
 * import org.yarnandtail.andhow.property.StrProp;
 * 
 * public class SetFixedValues implements AndHowInit {
 * 
 * {@literal @}Override public AndHowConfiguration getConfiguration() {
 *   return  StdConfig.instance()
 *     .addFixedValue(MY_PROP, "some value");  //MY_PROP is some visible property
 *   }
 * }
 * </pre>
 * Alternatively you can use {@code AndHow.findConfig()} at an application entry
 * point such as the main method.
 * 
 * <h4>This is a Standard Loader</h4>
 * Like all {@code StandardLoader}'s, this loader is intended to be auto-created
 * by AndHow.  The set of standard loaders and their order can bet set
 * via the {@code AndHowConfiguration.setStandardLoaders()} methods.
 * Other loaders which don't implement the {@code StandardLoader} interface can
 * be inserted into the load order via the
 * {@code AndHowConfiguration.insertLoaderBefore/After()}.
 * 
 * @author ericeverman
 */
public class StdFixedValueLoader extends FixedValueLoader implements StandardLoader {

	/**
	 * There is no reason to use the constructor in production application code
	 * because AndHow creates a single instance on demand at runtime.
	 */
	public StdFixedValueLoader() {
	}

}
