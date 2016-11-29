package yarnandtail.andhow.load;

import yarnandtail.andhow.LoaderException;
import java.util.List;
import yarnandtail.andhow.AppConfigStructuredValues;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.LoaderValues.PointValue;
import yarnandtail.andhow.appconfig.AppConfigDefinition;
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
	public LoaderValues load(AppConfigDefinition appConfigDef, List<String> cmdLineArgs,
			AppConfigStructuredValues existingValues, List<LoaderException> loaderExceptions) {
		return new LoaderValues(this, values);
	}
	
	
	
}
