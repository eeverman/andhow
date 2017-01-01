package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import yarnandtail.andhow.GroupInfo;
import yarnandtail.andhow.LoaderProblem;
import yarnandtail.andhow.LoaderProblem.IOLoaderProblem;
import yarnandtail.andhow.LoaderProblem.NoJndiContextLoaderProblem;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.Property;
import yarnandtail.andhow.PropertyGroup;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.TextUtil;
import yarnandtail.andhow.ValueMap;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.ValueMapWithContext;
import yarnandtail.andhow.property.QuotedSpacePreservingTrimmer;
import yarnandtail.andhow.property.StrProp;

/**
 * Loads values from a JNDI context.
 * 
 * This loader can handle two types of objects coming from JNDI:  Either the incoming
 * object must already be of the correct destination type (such as a DateTime object),
 * or it my be a string that is parsable by the associated ValueType to the destination
 * type.
 * 
 * If the incoming value is a String and the destination type is a string, this 
 * loader does not trim the value - the value is assumed to already be in final form.
 * This loader does not consider it a problem to find unrecognized properties
 * in the JNDI context (this would nearly always be the case).
 * 
 * @author eeverman
 */
public class JndiLoader extends BaseLoader {
	
	static String JNDI_PROTOCOL_NAME = "java:";
	
	
	@Override
	public LoaderValues load(RuntimeDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		
		ArrayList<String> jndiRoots = buildJndiRoots(existingValues);
		
		ArrayList<PropertyValue> values = new ArrayList();
		ArrayList<LoaderProblem> problems = new ArrayList(0);
		
		try {
			InitialContext ctx = new InitialContext();
			
			for (Property<?> p : appConfigDef.getProperties()) {
				String propName = appConfigDef.getCanonicalName(p);
				
				for (String root : jndiRoots) {
					try {
						Object o = ctx.lookup(JNDI_PROTOCOL_NAME + root + propName);
						
						if (o != null) {
							attemptToAdd(appConfigDef, values, problems, p, o);
						}
						
					} catch (NameNotFoundException nnf) {
						//Ignore - this is expected
					} catch (NamingException ex) {
						problems.add(new IOLoaderProblem(this, appConfigDef.getGroupForProperty(p), p, ex));
					}
				}
				
			}
			
			
		} catch (NamingException ex) {
			problems.add(new NoJndiContextLoaderProblem(this));
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
			name="JndiLoader Configuration",
			desc="Since application containers use various JNDI roots to store "
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
