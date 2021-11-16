package org.yarnandtail.andhow.load.std;

import java.util.*;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.MapLoader;

/**
 * Reads the Java system properties and loads the value for any system property
 * with a name matching a Property.
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
 * <p><b><em>Property value loading is based on a 'first win' strategy, so the first
 * loader to find a non-null value for a property sets the value.</em></b></p>
 * 
 * <h3>Typical Use Case</h3>
 * <p>
 * An application might receive all or some of its configuration from Java system properties that
 * are set when the JVM starts up.  Java system properties can be set in a startup script,
 * which could be customized for each environment.  This provides a relatively easy way for
 * deployment automation or system administrators to control application configuration values across
 * many servers.
 * </p>
 * <h3>Basic Behaviors</h3>
 * <ul>
 * <li><b>Trims String values: Yes</b>  The Trimmer configured for a String based property will
 * be applied as the value is loaded.  For most String-based properties this defaults to
 * {@link org.yarnandtail.andhow.property.QuotedSpacePreservingTrimmer}.
 * Non-String values are always trimmed to null.
 * <li><b>Complains about unrecognized properties: No</b>
 * </ul>
 * 
 * <h3>Loader Details and Configuration</h3>
 * <p>
 * This loader loads properties from the values it finds in {@code java.lang.System.getProperties()}
 * at the time AndHow is initialized.  Over the lifecycle of the JVM, the values of system properties
 * can change so they may not reflect the values AndHow has loaded.  However,
 * <em>once loaded, AndHow property values never change.</em>  It is also possible for
 * application code to add to or modify system properties after the JVM starts up and before
 * AndHow initializes, potentially resulting in a AndHow's configuration not matching the values set
 * in a JVM startup script.
 * </p>
 * <p>
 * For {@code FlgProp} properties (true/false flags), the {@code StdSysPropLoader}
 * will set the Property's value to true if a matching environment variable is found,
 * even if the value of the property is empty.  System properties can be cleared via
 * {@code java.lang.System.clearProperty(name)}, which is how a flag value could
 * be unset prior to AndHow loading.
 * </p>
 * <p>
 * Passing system properties on command line looks like this:
 * <pre>java -Dfull.name.of.MY_PROPERTY=someValue -jar MyJarName.jar</pre>
 * </p>
 * <h3>This is a Standard Loader</h3>
 * Like all {@code StandardLoader}'s, this loader is intended to be auto-created
 * by AndHow.  The set of standard loaders and their order can bet set
 * via the {@code AndHowConfiguration.setStandardLoaders()} methods.
 * Other loaders which don't implement the {@code StandardLoader} interface can
 * be inserted into the load order via the
 * {@code AndHowConfiguration.insertLoaderBefore/After()}.
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
		return true;
	}
	
	@Override
	public String getLoaderType() {
		return "SystemProperty";
	}
	
}
