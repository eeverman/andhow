package yarnandtail.andhow.load;

import yarnandtail.andhow.ParsingException;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.*;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.ValueProblem;
import yarnandtail.andhow.internal.RuntimeDefinition;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<? extends PropertyGroup> getLoaderConfig() {
		return null;
	}
	
	protected void attemptToAdd(RuntimeDefinition appConfigDef, List<PropertyValue> values, 
			String key, String strValue) throws ParsingException {
		
		key = TextUtil.trimToNull(key);
		
		if (key != null && strValue != null) {
			Property prop = appConfigDef.getProperty(key);

			if (prop != null) {
				
				PropertyValue pv = createValue(prop, strValue);
				values.add(pv);
				
			} else {
				//need a way to deal w/ these
			}

		}
	}
	
	protected <T> PropertyValue createValue(Property<T> prop, String strValue) throws ParsingException {
		
		T value = prop.getValueType().convert(strValue);
		
		ArrayList<ValueProblem> issues = new ArrayList();

		for (Validator<T> v : prop.getValidators()) {
			if (! v.isValid(value)) {
				issues.add(new ValueProblem(this, prop, value, v));
			}
		}
		
		return new PropertyValue(prop, value, issues);
	}
	
}
