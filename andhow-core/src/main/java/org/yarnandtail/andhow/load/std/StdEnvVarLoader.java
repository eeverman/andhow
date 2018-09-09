package org.yarnandtail.andhow.load.std;

import java.util.*;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.MapLoader;

/**
 * Reads the operating system defined environment variables and loads the value
 * for any environmental variable who's name matches a Property.
 * <h3>Position in Standard Loading Order, first to last</h3>
 * <ul>
 * <li>StdFixedValueLoader
 * <li>StdMainStringArgsLoader
 * <li>StdSysPropLoader
 * <li><b>StdEnvVarLoader &lt;-- This loader</b>
 * <li>StdJndiLoader
 * <li>StdPropFileOnFilesystemLoader
 * <li>StdPropFileOnClasspathLoader
 * </ul>
 * <h3>Typical Use Case</h3>
 * An application might receive all or some of its configuration from OS defined
 * environmental variables that are passed to the JVM when the JVM starts.
 * Environment variables can be set in the OS and augmented in a startup script,
 * which could be customized for each environment.
 * Similar to Java system properties, this provides a relatively easy way for
 * deployment automation or system administrators to control application
 * configuration values across many servers.
 * <h3>Basic Behaviors</h3>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: No</b>
 * <li><b>Default behavior:  AndHow always attempts to read environment
 * variables and will assign property values if any of them match known property names.</b>
 * </ul>
 * <h3>Loader Details and Configuration</h3>
 * This loader loads values from {@code System.getenv()}.
 * Those environmental values are provided by the host environment (the OS)
 * as a static snapshot to the JVM at startup as a String-to-String map.
 * The underlying OS environment variables can change, however, the JVM is
 * unaware of it.  Similarly, <em>AndHow property values never change once loaded.</em>
 * <br>
 * For {@code FlgProp} properties (true/false flags), the {@code StdEnvVarLoader}
 * will set the Property's value to true if a matching environment variable is
 * found, even if the value of the property is empty.
 * <br>
 * The Windows OS uses ALL CAPS for environment variables, while all others OS's
 * are case sensitive - This is the primary reason AndHow is case insensitive
 * by default.  Each OS <a href="https://www.schrodinger.com/kb/1842">
 * has a different way to set an environmental variables</a>
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
public class StdEnvVarLoader extends MapLoader implements StandardLoader {
	
	/**
	 * There is no reason to use the constructor in production application code
	 * because AndHow creates a single instance on demand at runtime.
	 */
	public StdEnvVarLoader() {
		unknownPropertyAProblem = false;
	}
	
	@Override
	public Map<?, ?> getMap() {
		if (map != null) {
			return map;
		} else {
			return System.getenv();
		}
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "java.lang.System.getenv()";
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}
	
	@Override
	public String getLoaderType() {
		return "EnvironmentVariable";
	}

}
