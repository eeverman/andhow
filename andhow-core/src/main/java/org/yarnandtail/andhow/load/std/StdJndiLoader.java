package org.yarnandtail.andhow.load.std;

import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;
import java.util.ArrayList;
import java.util.List;
import javax.naming.*;
import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem.JndiContextLoaderProblem;
import org.yarnandtail.andhow.load.BaseLoader;
import org.yarnandtail.andhow.property.QuotedSpacePreservingTrimmer;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.sample.JndiLoaderSamplePrinter;
import org.yarnandtail.andhow.util.AndHowLog;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Attempts to look up the name of each known {@code Property} in the JNDI
 * environment and loads the value for any that are found.
 * <h3>Position in Standard Loading Order, first to last</h3>
 * <ul>
 * <li>StdFixedValueLoader
 * <li>StdMainStringArgsLoader
 * <li>StdSysPropLoader
 * <li>StdEnvVarLoader
 * <li><b>StdJndiLoader &lt;-- This loader</b>
 * <li>StdPropFileOnFilesystemLoader
 * <li>StdPropFileOnClasspathLoader
 * </ul>
 * <h3>Typical Use Case</h3>
 * A web or service application runs in an application container such as Tomcat.
 * Application containers provide a JNDI environment which can be used to
 * configure applications running in their environment.
 * <h3>Basic Behaviors</h3>
 * <ul>
 * <li><b>Pre-trims String values: No</b> (Individual Properties may still trim values)
 * <li><b>Complains about unrecognized properties: No</b>
 * <li><b>Complains about missing JNDI environment:  No</b> (by default)
 * <li><b>Default behavior:  Always attempts to look up each Property in the JNDI environment</b>
 * <li><b>Is case sensitive: Yes</b> (This is one of the only loaders that is case sensitive)
 * </ul>
 * <h3>Loader Details and Configuration</h3>
 * While most other loaders are case insensitive, the JNDI loader is case
 * sensitive because the JNDI API is case sensitive.
 * Also, while most other loaders consume a configuration resource
 * (e.g. a properties file) and read all the names and values, the JNDI loader
 * works the other way:  It goes through the list of known Properties looks up
 * each property name in the JNDI context.  This because its not possible to
 * somehow read the entire JNDI environment.
 * <br>
 * JNDI implementations vary in how they name properties, so the loader will
 * try several common name forms, for example, the JNDI loader will attempt to
 * look up the following JNDI names for a property named {@code org.foo.My_Prop}:
 * <ul>
 * <li>{@code java:comp/env/org/foo/My_Prop}
 * <li>{@code java:comp/env/org.foo.My_Prop}
 * <li>{@code java:org/foo/My_Prop}
 * <li>{@code java:org.foo.My_Prop}
 * <li>{@code org/foo/My_Prop}
 * <li>{@code org.foo.My_Prop}
 * </ul>
 * This list has two different styles of property names:  dot separated
 * 'AndHow style' and slash separated JNDI style.  Additionally, AndHow looks
 * for three different roots (the part that comes before the variable name):
 * {@code java:comp/env/} is used by Tomcat and several other application servers,
 * {@code java:} is used by some non-container environments and at least one
 * application server (Glassfish) uses no root at all.  In all, AndHow will
 * search for each property under six different names (2 X 3).
 * AndHow will throw an error at startup if it finds multiple names in the
 * JNDI context that refer to the same property.
 * <br>
 * Specifying JNDI environment variables varies by environment, but here is an
 * example of specifying some properties in a Tomcat context.xml file:
 * <pre>{@code
 * <Context>
 * . . .
 *   <Environment name="org/simple/GettingStarted/COUNT_DOWN_START" value="3" type="java.lang.Integer" override="false"/>
 *   <Environment name="org/simple/GettingStarted/LAUNCH_CMD" value="GoGoGo!" type="java.lang.String" override="false"/>
 * . . .
 * </Context>
 * }</pre>
 * <br>
 * In the example above, Tomcat will automatically prepend {@code java:comp/env/}
 * to the name it associates with each value.  As the example shows, JNDI values
 * can be typed.  If AndHow finds the value to already be the type it expects
 * (e.g. an {@code Integer}), great!  If AndHow finds a String and needs a
 * different type, AndHow will do the conversion.  Any other type of conversion
 * (e.g. from a {@code Short} to an {@code Integer}) will result in an exception.
 * <br>
 * If your JNDI environment uses a non-default different root, it can be added
 * using one of the built-in Properties for the JNDI loader.  Those property
 * values would need to be loaded prior to the JNDI loader,
 * so using system properties, for example, would work.  Here is an example of
 * adding the custom JNDI root {@code java:xyz/} as a system property on command line:
 * <pre>
 * java -Dorg.yarnandtail.andhow.load.std.StdJndiLoader.CONFIG.ADDED_JNDI_ROOTS=java:xyz/ -jar MyJarName.jar
 * </pre>
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
public class StdJndiLoader extends BaseLoader implements LookupLoader, StandardLoader {

	private boolean failedEnvironmentAProblem = false;

	/**
	 * There is no reason to use the constructor in production application code
	 * because AndHow creates a single instance on demand at runtime.
	 */
	public StdJndiLoader() {
	}

	@Override
	public LoaderValues load(PropertyConfigurationInternal appConfigDef, ValidatedValuesWithContext existingValues) {

		AndHowLog log = AndHowLog.getLogger(StdJndiLoader.class);

		ArrayList<ValidatedValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();

		try {

			InitialContext ctx = new InitialContext();	//Normally doesn't throw exception, even if no JNDI

			ctx.getEnvironment();	//Should throw error if JNDI is unavailable

			List<String> jndiRoots = buildJndiRoots(existingValues);

			for (Property<?> prop : appConfigDef.getProperties()) {

				List<String> propJndiNames = buildJndiNames(appConfigDef, jndiRoots, prop);

				for (String propName : propJndiNames) {
					try {
						Object o = ctx.lookup(propName);

						if (o != null) {
							attemptToAdd(appConfigDef, values, problems, prop, o);
						}

					} catch (NamingException ne) {
						//Ignore - this is expected if a value is not found
					}
				}
			}

		} catch (NamingException  ex) {

			if (isFailedEnvironmentAProblem()) {
				Problem p = new JndiContextLoaderProblem(this);
				log.error(p.getProblemDescription(), ex);
				problems.add(p);
			} else {
				log.debug("No JNDI Environment found, or a naming error encountered. " +
					"The JndiLoader is configured to ignore this.");
			}
		}

		return new LoaderValues(this, values, problems);
	}

	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}

	@Override
	public String getSpecificLoadDescription() {
		return "JNDI properties in the system-wide JNDI context";
	}

	@Override
	public Class<?> getClassConfig() {
		return CONFIG.class;
	}

	@Override
	public SamplePrinter getConfigSamplePrinter() {
		return new JndiLoaderSamplePrinter();
	}

	/**
	 * Combines the values of STANDARD_JNDI_ROOTS and ADDED_JNDI_ROOTS into one list of jndi root contexts to search.
	 *
	 * Expected values might look like:  java:  or java:/comp/env/
	 *
	 * @param values The configuration state.
	 * @return Never null and never non-empty.
	 */
	protected List<String> buildJndiRoots(ValidatedValues values) {
		ArrayList<String> myJndiRoots = new ArrayList();

		//Add the added roots to the search list first, since they are pretty
		//likely to be the correct ones if someone explicitly added them.
		//We still check them all anyway, since a duplicate entry would be ambiguous.

		if (values.getValue(CONFIG.ADDED_JNDI_ROOTS) != null) {
			List<String> addRoots = split(values.getValue(CONFIG.ADDED_JNDI_ROOTS));
			myJndiRoots.addAll(addRoots);
		}

		List<String> addRoots = split(values.getValue(CONFIG.STANDARD_JNDI_ROOTS));
		myJndiRoots.addAll(addRoots);

		return myJndiRoots;
	}

	/**
	 * Builds a complete list of complete JNDI names to search for a parameter value.
	 *
	 * @param appConfigDef
	 * @param roots
	 * @param prop
	 * @return An ordered list of jndi names, with (hopefully) the most likely names first.
	 */
	protected List<String> buildJndiNames(PropertyConfigurationInternal appConfigDef, List<String> roots, Property prop) {

		List<String> propNames = new ArrayList();		// w/o jndi root prefix
		List<String> propJndiNames = new ArrayList();	// w/ jndi root prefix - return value

		//Check the URI name first (more likely), then the classpath style name
		if (appConfigDef.getNamingStrategy().isUriNameDistinct(appConfigDef.getCanonicalName(prop))) {
			propNames.add(appConfigDef.getNamingStrategy().getUriName(appConfigDef.getCanonicalName(prop)));
		}

		propNames.add(appConfigDef.getCanonicalName(prop));

		//Add all of the 'in' aliases
		appConfigDef.getAliases(prop).stream().filter(a -> a.isIn()).forEach(a -> {
			propNames.add(a.getActualName());

			//Add the URI style name if it is different
			if (appConfigDef.getNamingStrategy().isUriNameDistinct(a.getActualName())) {
				propNames.add(appConfigDef.getNamingStrategy().getUriName(a.getActualName()));
			}
		});

		for (String root : roots) {

			for (String propName : propNames) {
				propJndiNames.add(root + propName);
			}
		}

		return propJndiNames;

	}

	/**
	 * Spits a comma separate list of JNDI roots into individual root strings.
	 *
	 * Use double quotes to indicate and preserve and empty or white space string.
	 *
	 * @param rootStr
	 * @return A list of JNDI root strings
	 */
	protected List<String> split(String rootStr) {

		List<String> cleanRoots = new ArrayList();

		if (rootStr != null) {
			QuotedSpacePreservingTrimmer trimmer = QuotedSpacePreservingTrimmer.instance();
			String[] roots = rootStr.split(",");

			for (int i = 0; i < roots.length; i++) {
				String s = trimmer.trim(roots[i]);
				if (s != null) cleanRoots.add(s);
			}

			return cleanRoots;
		} else {
			return TextUtil.EMPTY_STRING_LIST;
		}

	}

	@GroupInfo(
			name = "JndiLoader Configuration",
			desc = "Since JNDI providers use different base URIs to store "
			+ "entries, base URIs must be configurable. "
			+ "The most common URI roots are \"java:\", \"java:comp/env/\" or just \"\"."
			+ "To preserve whitespace or indicate an empty string, use double quotes around an individual comma separated value."
			+ "If your container/provider uses something different, set one of these properties. "
			+ "All configured JNDI roots will be searched for each application property."
			+ "Typically there are multiple roots to search and multiple forms of "
			+ "property names, leading to the possibility of duplicate/conflicting JNDI entries. "
			+ "If multiple entries are found in JNDI for a property, a runtime error is thrown at startup.")
	public static interface CONFIG {

		StrProp STANDARD_JNDI_ROOTS = StrProp.builder()
				.defaultValue("java:comp/env/,java:,\"\"").notNull()
				.desc("A comma separated list of standard JNDI root locations to be searched for properties. "
						+ "Setting this property will replace the standard list, "
						+ "use ADDED_JNDI_ROOTS to only add to the list. ")
				.helpText("The final JNDI URIs to be searched will look like this '[root][Property Name]'").build();

		StrProp ADDED_JNDI_ROOTS = StrProp.builder()
				.desc("A comma separated list of JNDI root locations to be prepended to the standard list for searching. "
						+ "Setting this property does not affect the STANDARD_JNDI_ROOTS.")
				.helpText("The final JNDI URIs to be searched will look like this '[root][Property Name]'").build();

	}

	@Override
	public String getLoaderType() {
		return "JNDI";
	}

	@Override
	public String getLoaderDialect() {
		return null;
	}

	@Override
	public void setFailedEnvironmentAProblem(boolean isAProblem) {
		failedEnvironmentAProblem = isAProblem;
	}

	@Override
	public boolean isFailedEnvironmentAProblem() {
		return failedEnvironmentAProblem;
	}

}
