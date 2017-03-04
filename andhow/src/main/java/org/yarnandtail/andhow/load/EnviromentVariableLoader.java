package org.yarnandtail.andhow.load;

import java.util.*;
import org.yarnandtail.andhow.*;

/**
 * Loads properties from java.lang.System.getenv().
 * 
 * The System environment variables are variables provided by the host environment.
 * They may contain system information and can also be used to pass custom user
 * variables from the host system to the JVM.
 * 
 * @See https://docs.oracle.com/javase/tutorial/essential/environment/env.html
 * 
 * This loader does not trim incoming values for String type properties - they are
 * assumed to already be in final form.
 * This loader does not consider it a problem to find unrecognized properties
 * in System.properties (this would nearly always be the case).
 * 
 * Since the variables are provided by the host system, there is not concept of
 * an incoming null value from the environment variables.
 * 
 * For FlgProp properties (flags), the SystemEnviromentLoader will interpret the
 * presence of the property name being present in the environment variables as
 * setting the property true, even if the value of the property is empty.
 * 
 * @author eeverman
 */
public class EnviromentVariableLoader extends BaseLoader {
	
	public EnviromentVariableLoader() {
	}
	
	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		
		ArrayList<PropertyValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();
		
		Map<String, String> props = appConfigDef.getSystemEnvironment();
		Set<String> keys = props.keySet();
		for(String key : keys) {
			if (key != null) {
				String val = props.get(key);

				attemptToAdd(appConfigDef, values, problems, key, val);
			}
		}

		values.trimToSize();
		
		return new LoaderValues(this, values, problems);
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
	public boolean isUnrecognizedPropertyNamesConsideredAProblem() {
		return false;
	}
}
