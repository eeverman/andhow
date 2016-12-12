package yarnandtail.andhow.load;

import yarnandtail.andhow.LoaderException;
import java.util.List;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.ValueMapWithContext;
//import yarnandtail.andhow.*;

/**
 * A utility loader that is used internally to put fixed values into the effective
 * list of values.
 * 
 * @author eeverman
 */
public class FixedValueLoader extends BaseLoader {

	List<PointValue> values;
			
	public FixedValueLoader(List<PointValue> values) {
		this.values = values;
	}
	
	@Override
	public LoaderValues load(RuntimeDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues, List<LoaderException> loaderExceptions) {
		return new LoaderValues(this, values);
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "a list of fixed values passed in by the construction code (not dynamically loaded)";
	}
	
}
