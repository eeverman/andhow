package org.yarnandtail.andhow.load.std;

import java.util.*;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.MapLoader;

/**
 * Reads the Java system properties and loads the value for any system property
 * who's name matches a Property.
 * <h3>Position in Standard Loading Order, first to last</h3>
 * <ul>
 * <li>StdFixedValueLoader
 * <li>StdMainStringArgsLoader
 * <li><b>StdSysPropLoader &lt;-- This loader</b>
 * <li>StdEnvVarLoader
 * <li>StdJndiLoader
 * <li>StdPropFileOnFilesystemLoader
 * <li>StdPropFileOnClasspathLoader
 * </ul>
 * <em>Property value loading is based on a 'first win' strategy, so the first
 * loader to find a value for a property sets the value.</em>
 * 
 * <h3>Typical Use Case</h3>
 * An application might receive all or some of its configuration from Java
 * system properties that are set when the JVM starts.  Java system properties
 * can be set in a startup script, which could be customized for each environment.
 * This provides a relatively easy way for deployment automation or system
 * administrators to control application configuration values across many servers.
 * <h3>Basic Behaviors</h3>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: No</b>
 * <li><b>Default behavior:  AndHow always attempts to read Java system
 * properties and will assign property values if any of the system properties
 * match known property names.</b>
 * </ul>
 * 
 * <h3>Loader Details and Configuration</h3>
 * This loader loads properties from {@code java.lang.System.getProperties()}.
 * Over the lifecycle of the JVM, values of system properties can change
 * so this loader is working from a snapshot of the system properties it finds
 * at the time of AndHow initialization.
 * <em>Once loaded, AndHow property values never change.</em>
 * <br>
 * For {@code FlgProp} properties (true/false flags), the {@code StdSysPropLoader}
 * will set the Property's value to true if a matching environment variable is found,
 * even if the value of the property is empty.  System properties can be cleared via
 * {@code java.lang.System.clearProperty(name)}, which is how a flag value could
 * be unset prior to AndHow loading.
 * <br>
 * Passing system properties on command line looks like this:
 * <pre>java -Dfull.name.of.MY_PROPERTY=someValue -jar MyJarName.jar</pre>
 * 
 * <h3>This is a Standard Loader</h3>
 * Like all {@code StandardLoader}'s, this loader is intended to be auto-created
 * by AndHow.  The set of standard loaders and their order can bet set
 * via the {@code AndHowConfiguration.setStandardLoaders()} methods.
 * Other loaders which don't implement the {@code StandardLoader} interface can
 * be inserted into the load order via the
 * {@code AndHowConfiguration.insertLoaderBefore/After()}.
 *
 * @author eeverman
 */
public class StdSysPropLoader extends MapLoader implements StandardLoader {
	
	/**
	 * There is no reason to use the constructor in production application code
	 * because AndHow creates a single instance on demand at runtime.
	 */
	public StdSysPropLoader() {
		unknownPropertyAProblem = false;
	}
	
	@Override
	public Map<?, ?> getMap() {
		if (map != null) {
			return map;
		} else {
			return System.getProperties();
		}
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "java.lang.System.getProperties()";
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}
	
	@Override
	public String getLoaderType() {
		return "SystemProperty";
	}
	
}
