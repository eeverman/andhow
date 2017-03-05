package org.yarnandtail.andhow.load;

import java.util.Arrays;
import java.util.List;
import org.yarnandtail.andhow.*;

/**
 * A utility loader that is used internally to put fixed values into the effective
 * list of values.
 * 
 * This loader does not trim incoming values for String type properties - they are
 * assumed to already be in final form.
 * This loader considers it a problem to be passed unrecognized properties
 * and will throw a RuntimeException if that happens.
 * 
 * @author eeverman
 */
public class FixedValueLoader extends BaseLoader {

	List<PropertyValue> values;
			
	public FixedValueLoader(List<PropertyValue> values) {
		this.values = values;
	}
	
	public FixedValueLoader(PropertyValue... values) {
		this.values = Arrays.asList(values);
	}
	
	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		return new LoaderValues(this, values, ProblemList.EMPTY_PROBLEM_LIST);
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return false;
	}
	
	@Override
	public boolean isUnrecognizedPropertyNamesConsideredAProblem() {
		return true;
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "a list of fixed values passed in by the construction code (not dynamically loaded)";
	}
	
	@Override
	public String getLoaderType() {
		return "FixedValue";
	}
	
	@Override
	public String getLoaderDialect() {
		return "FromJavaSourceCode";
	}
	
}
