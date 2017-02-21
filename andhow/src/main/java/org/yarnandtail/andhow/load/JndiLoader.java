package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.Problem;
import org.yarnandtail.andhow.ProblemList;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.ConstructionDefinition;
import org.yarnandtail.andhow.LoaderValues;
import org.yarnandtail.andhow.SamplePrinter;
import org.yarnandtail.andhow.ValueMapWithContext;
import org.yarnandtail.andhow.Property;
import org.yarnandtail.andhow.ValueMap;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.sample.JndiLoaderSamplePrinter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.yarnandtail.andhow.internal.LoaderProblem.JndiContextLoaderProblem;
import org.yarnandtail.andhow.util.TextUtil;
import org.yarnandtail.andhow.property.QuotedSpacePreservingTrimmer;
import org.yarnandtail.andhow.property.StrProp;

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
public class JndiLoader extends BaseLoader {

	static String JNDI_PROTOCOL_NAME = "java:";

	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {

		ArrayList<String> jndiRoots = buildJndiRoots(existingValues);

		ArrayList<PropertyValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();

		try {
			InitialContext ctx = new InitialContext();
			List<String> propNames = new ArrayList();

			for (Property<?> prop : appConfigDef.getProperties()) {

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

				for (String root : jndiRoots) {

					for (String propName : propNames) {
						try {
							Object o = ctx.lookup(JNDI_PROTOCOL_NAME + root + propName);

							if (o != null) {
								attemptToAdd(appConfigDef, values, problems, prop, o);
							}

						} catch (NameNotFoundException nnf) {
							//Ignore - this is expected
						}
					}
				}

				propNames.clear();

			}

		} catch (NamingException ex) {
			//This is fatal and means there likely is no JNDI context
			problems.add(new JndiContextLoaderProblem(this));	//Not sure why this would happen
		}

		return new LoaderValues(this, values, problems);
	}

	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}

	@Override
	public boolean isUnrecognizedPropertyNamesConsideredAProblem() {
		return false;
	}

	@Override
	public String getSpecificLoadDescription() {
		return "JNDI properties in the system-wide JNDI context";
	}

	@Override
	public Class<? extends PropertyGroup> getLoaderConfig() {
		return CONFIG.class;
	}

	@Override
	public SamplePrinter getConfigSamplePrinter() {
		return new JndiLoaderSamplePrinter();
	}

	protected ArrayList<String> buildJndiRoots(ValueMap values) {
		ArrayList<String> myJndiRoots = new ArrayList();

		List<String> someRoots = split(CONFIG.STANDARD_JNDI_ROOTS.getValue(values));
		myJndiRoots.addAll(someRoots);

		String addRoots = CONFIG.ADDED_JNDI_ROOTS.getValue(values);

		if (addRoots != null) {
			someRoots = split(addRoots);
			myJndiRoots.addAll(someRoots);
		}

		return myJndiRoots;
	}

	protected List<String> split(String rootStr) {

		if (rootStr != null) {
			QuotedSpacePreservingTrimmer trimmer = QuotedSpacePreservingTrimmer.instance();
			String[] roots = rootStr.split(",");

			for (int i = 0; i < roots.length; i++) {
				roots[i] = trimmer.trim(roots[i]);

				if (roots[i].length() > 0 && !(roots[i].endsWith("/"))) {
					roots[i] = roots[i] + "/";
				}
			}

			return Arrays.asList(roots);
		} else {
			return TextUtil.EMPTY_STRING_LIST;
		}

	}

	@GroupInfo(
			name = "JndiLoader Configuration",
			desc = "Since application containers use various JNDI roots to store "
			+ "environment varibles, these properties allow customization. "
			+ "The most common roots are \"\" or \"comp/env/\". "
			+ "If your container uses something different, set one of these properties. "
			+ "All configured JNDI roots will be searched for each application property. "
			+ "For both properties, trailing slashes will automcatically be added, "
			+ "however, a leading slash is significant - "
			+ "is non-standard but allowed and your properties must match.")
	public static interface CONFIG extends PropertyGroup {

		StrProp STANDARD_JNDI_ROOTS = StrProp.builder()
				.defaultValue("comp/env/, \"\"")
				.desc("A comma separated list of standard JNDI root locations to be searched for properties. "
						+ "Setting this property will replace the standard list, "
						+ "use ADDED_JNDI_ROOTS to only add to the list. ")
				.helpText("The final JNDI URIs to be searched will look like this 'java:[root]/[Property Name]'").build();

		StrProp ADDED_JNDI_ROOTS = StrProp.builder()
				.desc("A comma separated list of JNDI root locations to be added to the standard list for searching. "
						+ "Setting this property does not affect the STANDARD_JNDI_ROOTS.")
				.helpText("The final JNDI URIs to be searched will look like this 'java:[root]/[Property Name]'").build();

	}

}
