package org.yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import org.yarnandtail.andhow.api.*;

/**
 * Loads properties from java.lang.System.getProperties().
 *
 * While the values of System.properties can change, this loader loads a
 * snapshot of the property values it finds at the time of the load. Changes to
 * System.properties will not affect the values after they are loaded.
 *
 * This loader does not trim incoming values for String type properties - they
 * are assumed to already be in final form. This loader does not consider it a
 * problem to find unrecognized properties in System.properties (this would
 * nearly always be the case).
 *
 * The HashMap backing System.Properties does not accept null values, so this
 * loader has no concept of any incoming null value.
 *
 * For FlgProp properties (flags), the SystemPropertyLoader will interpret the
 * presence of the property name as setting the property true, even if the value
 * of the property is empty. System properties can be cleared via
 * java.lang.System.clearProperty(name), which is how a flag value could be
 * unset prior to AndHow loading.
 *
 * @author eeverman
 */
public class SystemPropertyLoader extends BaseLoader {
	
	public SystemPropertyLoader() {
	}
	
	@Override
	public LoaderValues load(GlobalScopeConfiguration appConfigDef, PropertyValuesWithContext existingValues) {
		
		ArrayList<PropertyValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();
		
		Properties props = System.getProperties();
		Set<Object> keys = props.keySet();
		for(Object key : keys) {
			if (key != null) {
				String k = key.toString();
				String v = props.getProperty(k);

				attemptToAdd(appConfigDef, values, problems, k, v);
			}
		}

		values.trimToSize();
		
		return new LoaderValues(this, values, problems);
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
	public boolean isUnrecognizedPropertyNamesConsideredAProblem() {
		return false;
	}
	
	
	@Override
	public String getLoaderType() {
		return "SystemProperty";
	}
	
	@Override
	public String getLoaderDialect() {
		return null;
	}
}
