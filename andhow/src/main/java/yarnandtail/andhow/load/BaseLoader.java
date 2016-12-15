package yarnandtail.andhow.load;

import yarnandtail.andhow.ParsingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
				
				PropertyValue pv = createValue(appConfigDef, prop, strValue);
				values.add(pv);
				
			} else {
				//need a way to deal w/ these
			}

		}
	}
	
	protected <T> PropertyValue createValue(RuntimeDefinition appConfigDef, Property<T> prop, String strValue) {
		
		ArrayList<ValueProblem> issues = new ArrayList(0);
		T value = null;
		
		try {
			
			value = prop.getValueType().convert(strValue);

			for (Validator<T> v : prop.getValidators()) {
				if (! v.isValid(value)) {
					issues.add(new ValueProblem.InvalidValueProblem(
							this, appConfigDef.getGroupForProperty(prop), prop, value, v));
				}
			}
		} catch (ParsingException ex) {
			issues.add(new ValueProblem.StringConversionValueProblem(
					this, appConfigDef.getGroupForProperty(prop), prop, strValue));
		}
		

		
		return new PropertyValue(prop, value, issues);
	}
	
}
