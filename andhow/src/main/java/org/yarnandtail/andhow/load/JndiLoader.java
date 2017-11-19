package org.yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.List;
import javax.naming.*;
import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem.JndiContextLoaderProblem;
import org.yarnandtail.andhow.property.QuotedSpacePreservingTrimmer;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.sample.JndiLoaderSamplePrinter;
import org.yarnandtail.andhow.util.AndHowLog;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Loads values from a JNDI context.
 *
 * This loader can handle two types of objects coming from JNDI: Either the
 * incoming object must already be of the correct destination type (such as a
 * DateTime object), or it my be a string that is parsable by the associated
 * ValueType to the destination type.
 *
 * If the incoming value is a String and the destination type is a string, this
 * loader does not trim the value - the value is assumed to already be in final
 * form. This loader does not consider it a problem to find unrecognized
 * properties in the JNDI context (this would nearly always be the case).
 *
 * @author eeverman
 */
public class JndiLoader extends BaseLoader implements LookupLoader {

	private boolean failedEnvironmentAProblem = false;
	
	public JndiLoader() {
	}
	
	@Override
	public LoaderValues load(StaticPropertyConfiguration appConfigDef, ValidatedValuesWithContext existingValues) {

		AndHowLog log = AndHowLog.getLogger(JndiLoader.class);
		
		List<String> jndiRoots = buildJndiRoots(existingValues);

		ArrayList<ValidatedValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();

		try {
			InitialContext ctx = new InitialContext();
			List<String> propNames = new ArrayList();

			for (Property<?> prop : appConfigDef.getProperties()) {

				
				List<String> propJndiNames = buildJndiNames(appConfigDef, jndiRoots, prop);
				


				for (String propName : propJndiNames) {
					try {
						Object o = ctx.lookup(propName);

						if (o != null) {
							attemptToAdd(appConfigDef, values, problems, prop, o);
						}

					} catch (NameNotFoundException nnfe) {
						//Ignore - this is expected
					} catch (NamingException ne) {
						//Glassfish seems to be throwing this error w/
						//a root cause of NameNotFound for simple NNF exceptions.
						if (ne.getRootCause() instanceof NameNotFoundException) {
							//Ignore - expected
						} else {
							throw ne;
						}
					}
				}


			}

		} catch (NamingException ex) {
			if (isFailedEnvironmentAProblem()) {
				log.error(
						"Unable to read from JNDI - Does JNDI exist in this environment? "
								+ "If this is expected, initialize the JndiLoader ignore non-JNDI environments.", ex);
				problems.add(new JndiContextLoaderProblem(this));
			} else {
				log.debug("No JNDI Environment found, or a naming error encountered.  The JndiLoader is configured to ignore this.");
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
	protected List<String> buildJndiNames(StaticPropertyConfiguration appConfigDef, List<String> roots, Property prop) {
		
		List<String> propNames = new ArrayList();		// w/o jndi root prefix
		List<String> propJndiNames = new ArrayList();	// w/ jndi root prefix - return value

		//Check the URI name first (more likely), then the classpath style name
		if (appConfigDef.getNamingStrategy().isUriNameDistict(appConfigDef.getCanonicalName(prop))) {
			propNames.add(appConfigDef.getNamingStrategy().getUriName(appConfigDef.getCanonicalName(prop)));
		}

		propNames.add(appConfigDef.getCanonicalName(prop));

		//Add all of the 'in' aliases
		appConfigDef.getAliases(prop).stream().filter(a -> a.isIn()).forEach(a -> {
			propNames.add(a.getActualName());

			//Add the URI style name if it is different
			if (appConfigDef.getNamingStrategy().isUriNameDistict(a.getActualName())) {
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
	public static interface CONFIG extends BasePropertyGroup {

		StrProp STANDARD_JNDI_ROOTS = StrProp.builder()
				.defaultValue("java:comp/env/,java:,\"\"").mustBeNonNull()
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
