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
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.ValueMapWithContext;
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
public class JndiValueLoader extends BaseLoader {

	ArrayList<String> jndiRoots = new ArrayList();
	
	String[] DEFAULT_JNDI_ROOTS = new String[] {"comp/env/", ""};
	String JNDI_PROTOCOL_NAME = "java:";
			
	public JndiValueLoader() {
		jndiRoots.addAll(Arrays.asList(DEFAULT_JNDI_ROOTS));
	}
	
	@Override
	public LoaderValues load(RuntimeDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		
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
	
	@GroupInfo(
			name="JndiValueLoader Configuration",
			desc="Application containers use different JNDI root locations to store "
					+ "environment varibles.  The common ones use either the root \"\" or "
					+ "\"comp/env/\".  If your container uses something different, one"
					+ "of these properties must be set.")
	public static interface CONFIG extends PropertyGroup {
		StrProp STANDARD_JNDI_ROOTS = StrProp.builder()
				.defaultValue("comp/env/, \"\"")
				.desc("A comma separated list of standard JNDI root locations to be searched for properties. "
						+ "Setting this property will replace the standard list.  "
						+ "Use ADDED_JNDI_ROOTS to add others.  Leading and trailing slashes are optional.")
				.helpText("The final JNDI URIs will look like this 'java:[root]/[Property Name]").build();
		
		StrProp ADDED_JNDI_ROOTS = StrProp.builder()
				.desc("A comma separated list of JNDI root locations to be added to the standard list for searching. "
						+ "Setting this property does not affect the STANDARD_JNDI_ROOTS.  Leading and trailing slashes are optional.")
				.helpText("The final JNDI URIs will look like this 'java:[root]/[Property Name]").build();
		
	}
	
}
